package fr.iglee42.compressed.blockentities.modules;

import com.mojang.datafixers.util.Pair;
import fr.iglee42.compressed.utils.Box;
import fr.iglee42.compressed.utils.BoxesSaveData;
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
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        save(compoundTag,provider,false);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        save(tag,provider,true);
        return tag;
    }


    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        load(compoundTag,provider);
    }

    protected void load(CompoundTag tag, HolderLookup.Provider registries){
        if (tag.contains("box")) setClientBox(Box.CODEC.decode(RegistryOps.create(NbtOps.INSTANCE,registries),tag.get("box")).mapOrElse(Pair::getFirst, e->null));
    }

    protected void save(CompoundTag tag, HolderLookup.Provider registries, boolean forClient){
        if (forClient && getBox() != null){
            tag.put("box",Box.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE,registries),getBox()).mapOrElse(Function.identity(), e->new CompoundTag()));
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
