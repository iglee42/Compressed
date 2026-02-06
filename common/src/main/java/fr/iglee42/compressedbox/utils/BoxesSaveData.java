package fr.iglee42.compressedbox.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.packets.payloads.s2c.ClearPlayerCurrentBoxPayload;
import fr.iglee42.compressedbox.packets.payloads.s2c.SyncPlayerCurrentBoxPayload;
import fr.iglee42.compressedbox.registries.CBlocks;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BoxesSaveData extends SavedData {

    public static final int BOXES_OFFSET = 25;

    private ChunkPos lastPos = new ChunkPos(0,0);

    @Getter
    private List<Box> boxes = new ArrayList<>();
    private Map<UUID, Pair<ResourceKey<Level>,BlockPos>> playerEntries = new HashMap<>();

    private List<UUID> knownPlayers = new ArrayList<>();

    public BoxesSaveData(){
    }

    public BoxesSaveData(CompoundTag tag,HolderLookup.Provider registries){
        boxes = new ArrayList<>(Codec.list(Box.CODEC).decode(
                RegistryOps.create(NbtOps.INSTANCE,registries),
                tag.getList("boxes", CompoundTag.TAG_COMPOUND)
        ).mapOrElse(Pair::getFirst, e->new ArrayList<>()));
        lastPos = CExtraCodecs.CHUNK_POS.decode(NbtOps.INSTANCE,tag.get("lastPos")).getOrThrow().getFirst();
        playerEntries = new HashMap<>();
        CompoundTag entries = tag.getCompound("playerEntries");
        entries.getAllKeys().forEach(k->{
            UUID uuid = UUID.fromString(k);
            CompoundTag entry = entries.getCompound(k);
            BlockPos pos = NbtUtils.readBlockPos(entry,"pos").orElse(null);
            ResourceKey<Level> level = ResourceKey.codec(Registries.DIMENSION).decode(RegistryOps.create(NbtOps.INSTANCE,registries),entry.get("dimension")).mapOrElse(Pair::getFirst,p->null);

            if (pos != null && level != null){
                playerEntries.put(uuid,Pair.of(level,pos));
            }
        });
        knownPlayers = new ArrayList<>(Codec.list(UUIDUtil.STRING_CODEC).decode(NbtOps.INSTANCE,tag.getList("knownPlayers", CompoundTag.TAG_STRING)).mapOrElse(Pair::getFirst, e->new ArrayList<>()));
    }

    public static BoxesSaveData get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException(new IllegalAccessException("Boxes saved datas can only be accessed on server-side !"));
        }
        DimensionDataStorage storage = ((ServerLevel) level).getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(new Factory<>(BoxesSaveData::new,BoxesSaveData::new,DataFixTypes.LEVEL), CompressedBox.MODID+"_boxes");
    }

    public static EventResult prevActionOutOfBoxes(Level level, @Nullable Entity entity, @Nullable BlockPos blockPos) {
        if (level.isClientSide || !level.dimension().equals(Box.DIMENSION)) return EventResult.pass();
        BoxesSaveData manager = get(level);

        if (blockPos != null){
            if (!level.getBlockState(blockPos).is(CBlocks.WALL.get()) && !level.getBlockState(blockPos).is(Blocks.BEDROCK)) {
                Box box = manager.getBoxByBlockPos(blockPos);
                if (box == null) return EventResult.interruptFalse();
            }
        }
        if (entity instanceof Player player){
            if (manager.getBoxFromPlayer(player) == null) return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }

    public static void exitFromDimIfPlayerOutOfBox(Player player) {
        Level level = player.level();
        if (level.isClientSide || !level.dimension().equals(Box.DIMENSION) ) return;

        BoxesSaveData manager = get(level);
        if (manager.getBoxFromPlayer(player) == null) {
            manager.exitPlayerFromBox((ServerPlayer) player);
            player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.tried_escape_box").withStyle(ChatFormatting.RED)),false);
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("boxes",Codec.list(Box.CODEC).encodeStart(
                RegistryOps.create(NbtOps.INSTANCE,registries),
                boxes
        ).getOrThrow());
        tag.put("lastPos",CExtraCodecs.CHUNK_POS.encodeStart(NbtOps.INSTANCE,lastPos).getOrThrow());
        CompoundTag entries = new CompoundTag();

        playerEntries.forEach((uuid,p)->{
            CompoundTag entry = new CompoundTag();
            entry.put("pos",NbtUtils.writeBlockPos(p.getSecond()));
            entry.put("dimension",ResourceKey.codec(Registries.DIMENSION).encodeStart(RegistryOps.create(NbtOps.INSTANCE,registries),p.getFirst()).getOrThrow());

            entries.put(uuid.toString(),entry);
        });

        tag.put("playerEntries",entries);

        tag.put("knownPlayers",Codec.list(UUIDUtil.STRING_CODEC).encodeStart(NbtOps.INSTANCE,knownPlayers).getOrThrow());
        return tag;
    }


    public void tick(ServerLevel level){
      level.getServer().getPlayerList().getPlayers().forEach(sp->{
          Box box = getBoxFromPlayer(sp);
          if (box != null){
              NetworkManager.sendToPlayer(sp,new SyncPlayerCurrentBoxPayload(box));
          } else {
              NetworkManager.sendToPlayer(sp, ClearPlayerCurrentBoxPayload.INSTANCE);
          }
      });
    }

    public Box createBox(ServerPlayer player){
        ChunkPos nextPos = getNextPos();
        Box box = Box.builder().owner(player.getUUID()).minPos(new BlockPos(nextPos.getMinBlockX(),64,nextPos.getMinBlockZ())).maxPos(new BlockPos(nextPos.getMaxBlockX(),79,nextPos.getMaxBlockZ())).name(player.getGameProfile().getName() +"'s box").build();
        box.generate(player.level());
        boxes.add(box);
        setDirty();
        return box;
    }

    public @Nullable Box getBox(UUID id){
        return boxes.stream().filter(b->b.getId().equals(id)).findAny().orElse(null);
    }

    private ChunkPos getNextPos(){
        while (boxes.stream().anyMatch(b->new ChunkPos(b.getCenterPos(64)).distanceSquared(lastPos) < BOXES_OFFSET)){
            lastPos = new ChunkPos(lastPos.x + BOXES_OFFSET,lastPos.z + BOXES_OFFSET);
        }
        return lastPos;
    }

    public @Nullable Pair<ResourceKey<Level>,BlockPos> getPlayerEntryPoint(Player player){
        return playerEntries.getOrDefault(player.getUUID(),null);
    }

    public void removePlayerEntryPoint(Player player){
        playerEntries.remove(player.getUUID());
        setDirty();
    }

    public void addPlayerEntryPoint(Player player){
        playerEntries.put(player.getUUID(),Pair.of(player.level().dimension(),player.blockPosition()));
        setDirty();
    }

    @Nullable
    public Box getBoxByBlockPos(BlockPos pos){
        return boxes.stream().filter(b->BlockPos.betweenClosedStream(b.getMinPos(),b.getMaxPos()).anyMatch(bp->bp.equals(pos))).findAny().orElse(null);
    }

    @Nullable
    public Box getBoxFromPlayer(Player player){
        if (!player.level().dimension().equals(Box.DIMENSION)) return null;
        return getBoxByBlockPos(player.blockPosition());
    }

    public boolean isPlayerNew(Player player){
        return !knownPlayers.contains(player.getUUID());
    }

    public void registerKnownPlayer(Player player){
        knownPlayers.add(player.getUUID());
        setDirty();
    }

    public void exitPlayerFromBox(ServerPlayer player) {
        Pair<ResourceKey<Level>, BlockPos> pair = getPlayerEntryPoint(player);

        if (pair == null || pair.getFirst() == null || pair.getSecond() == null){
            player.displayClientMessage(Component.translatable("message.compressedbox.box_not_found_entry_point").withStyle(ChatFormatting.RED),true);
            pair = Pair.of(player.getRespawnDimension(),player.getRespawnPosition());
        }

        if (pair == null || pair.getFirst() == null || pair.getSecond() == null){
            pair = Pair.of(player.server.overworld().dimension(),player.server.overworld().getSharedSpawnPos());
        }

        ServerLevel dimension = player.getServer().getLevel(pair.getFirst());
        BlockPos pos = pair.getSecond();
        if (player.teleportTo(dimension,pos.getX() + 0.5,pos.getY() + 0.5,pos.getZ() + 0.5, Set.of(),player.getXRot(),player.getYRot())){
            removePlayerEntryPoint(player);
        }
    }
}
