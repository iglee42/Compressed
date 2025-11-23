package fr.iglee42.compressed.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import fr.iglee42.compressed.Compressed;
import fr.iglee42.compressed.packets.payloads.s2c.ClearPlayerCurrentBoxPayload;
import fr.iglee42.compressed.packets.payloads.s2c.SyncPlayerCurrentBoxPayload;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
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
    }

    public static BoxesSaveData get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException(new IllegalAccessException("Boxes saved datas can only be accessed on server-side !"));
        }
        DimensionDataStorage storage = ((ServerLevel) level).getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(new Factory<>(BoxesSaveData::new,BoxesSaveData::new,DataFixTypes.LEVEL), Compressed.MODID+"_boxes");
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
        Box box = Box.builder().owner(player.getUUID()).pos(getNextPos()).name(player.getGameProfile().getName() +"'s box").build();
        box.generate(player.level());
        boxes.add(box);
        setDirty();
        return box;
    }

    public @Nullable Box getBox(UUID id){
        return boxes.stream().filter(b->b.getId().equals(id)).findAny().orElse(null);
    }

    private ChunkPos getNextPos(){
        while (boxes.stream().anyMatch(b->b.getPos().distanceSquared(lastPos) < BOXES_OFFSET)){
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
    public Box getBoxByPos(ChunkPos pos){
        return boxes.stream().filter(b->b.getPos().equals(pos)).findAny().orElse(null);
    }

    @Nullable
    public Box getBoxByBlockPos(BlockPos pos){
        return getBoxByPos(new ChunkPos(pos));
    }

    @Nullable
    public Box getBoxFromPlayer(Player player){
        if (!player.level().dimension().equals(Box.DIMENSION)) return null;
        return getBoxByBlockPos(player.blockPosition());
    }

}
