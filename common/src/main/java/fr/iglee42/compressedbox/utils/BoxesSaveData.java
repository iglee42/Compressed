package fr.iglee42.compressedbox.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.packets.s2c.ClearPlayerCurrentBoxPacket;
import fr.iglee42.compressedbox.packets.s2c.SyncPlayerCurrentBoxPacket;
import fr.iglee42.compressedbox.registries.CNetworking;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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

    public BoxesSaveData(CompoundTag tag,Level storageLevel){
        boxes = new ArrayList<>();
        tag.getList("boxes", CompoundTag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).forEach(t->{
            boxes.add(Box.load(t));
        });
        CompoundTag lastPosTag = tag.getCompound("lastPos");
        lastPos = new ChunkPos(lastPosTag.getInt("x"),lastPosTag.getInt("z"));
        playerEntries = new HashMap<>();
        CompoundTag entries = tag.getCompound("playerEntries");
        entries.getAllKeys().forEach(k->{
            UUID uuid = UUID.fromString(k);
            CompoundTag entry = entries.getCompound(k);
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(entry.getString("dimension")));

            if (pos != null && level != null){
                playerEntries.put(uuid,Pair.of(level,pos));
            }
        });
        knownPlayers = new ArrayList<>();
        ListTag knownPlayersTag = tag.getList("knownPlayers", CompoundTag.TAG_INT_ARRAY);
        knownPlayersTag.stream().map(IntArrayTag.class::cast).forEach(t->knownPlayers.add(NbtUtils.loadUUID(t)));
    }

    public static BoxesSaveData get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException(new IllegalAccessException("Boxes saved datas can only be accessed on server-side !"));
        }
        ServerLevel storageLevel = ((ServerLevel) level).getServer().overworld();
        DimensionDataStorage storage = storageLevel.getDataStorage();
        return storage.computeIfAbsent(tag->new BoxesSaveData(tag,storageLevel),BoxesSaveData::new, CompressedBox.MODID+"_boxes");
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        ListTag boxesTag = new ListTag();
        boxes.forEach(b->{
            boxesTag.add(b.save());
        });
        tag.put("boxes",boxesTag);
        CompoundTag lastPosTag = new CompoundTag();
        lastPosTag.putInt("x", lastPos.x);
        lastPosTag.putInt("z", lastPos.z);
        tag.put("lastPos",lastPosTag);
        CompoundTag entries = new CompoundTag();

        playerEntries.forEach((uuid,p)->{
            CompoundTag entry = new CompoundTag();
            entry.put("pos",NbtUtils.writeBlockPos(p.getSecond()));
            entry.putString("dimension",p.getFirst().location().toString());

            entries.put(uuid.toString(),entry);
        });

        tag.put("playerEntries",entries);

        ListTag knownPlayersTag = new ListTag();
        knownPlayers.forEach(uuid->knownPlayersTag.add(NbtUtils.createUUID(uuid)));
        tag.put("knownPlayers",knownPlayersTag);
        return tag;
    }


    public void tick(ServerLevel level){
      level.getServer().getPlayerList().getPlayers().forEach(sp->{
          Box box = getBoxFromPlayer(sp);
          if (box != null){
              CNetworking.CHANNEL.sendToPlayer(sp,new SyncPlayerCurrentBoxPacket(box));
          } else {
              CNetworking.CHANNEL.sendToPlayer(sp, ClearPlayerCurrentBoxPacket.INSTANCE);
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
        while (boxes.stream().anyMatch(b->distanceSquared(new ChunkPos(b.getCenterPos(64)),lastPos) < BOXES_OFFSET)){
            lastPos = new ChunkPos(lastPos.x + BOXES_OFFSET,lastPos.z + BOXES_OFFSET);
        }
        return lastPos;
    }

    private long distanceSquared(ChunkPos a, ChunkPos b){
        long dx = a.x - b.x;
        long dz = a.z - b.z;
        return dx * dx + dz * dz;
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

}
