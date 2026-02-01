package fr.iglee42.compressedbox.blockentities.modules;

import com.mojang.datafixers.util.Pair;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import lombok.AccessLevel;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class Module extends BlockEntity {

    @Setter(AccessLevel.PRIVATE)
    private @Nullable Box clientBox = null;

    public Module(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        save(compoundTag,false);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        save(tag,true);
        return tag;
    }


    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        loadModule(compoundTag);
    }

    protected void loadModule(CompoundTag tag){
        if (tag.contains("box") && level != null && level.isClientSide) setClientBox(Box.CODEC.decode(RegistryOps.create(NbtOps.INSTANCE,level.registryAccess()),tag.get("box")).result().map(Pair::getFirst).orElse(null));
    }

    protected void save(CompoundTag tag, boolean forClient){
        if (forClient && getBox() != null && level != null){
            tag.put("box",Box.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE,level.registryAccess()),getBox()).result().map(Function.identity()).orElse(new CompoundTag()));
        }
    }

    protected void clientTick(Level level,BlockPos pos,BlockState state){};
    protected void serverTick(ServerLevel level, BlockPos pos, BlockState state){};

    public void tick(Level level,BlockPos pos,BlockState state){
        if (level == null) return;
        if (!level.dimension().equals(Box.DIMENSION)) return;
        if (getBox() == null) return;
        if (level.isClientSide) clientTick(level,pos,state);
        else {
            serverTick((ServerLevel) level, pos, state);
            level.sendBlockUpdated(pos,state,state,2);
        }
    }

    @Nullable
    public Box getBox(){
        if (level == null) return null;
        if (level.isClientSide){
            if (clientBox == null){
                return null;
            }
            return clientBox;
        }
        BoxesSaveData data = BoxesSaveData.get(level);
        return data.getBoxByBlockPos(getBlockPos());
    }

    public void added(){};
    public void removed(){};





}
