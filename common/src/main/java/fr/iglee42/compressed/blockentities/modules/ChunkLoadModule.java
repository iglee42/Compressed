package fr.iglee42.compressed.blockentities.modules;

import fr.iglee42.compressed.blocks.modules.ChunkLoaderModuleBlock;
import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.registries.CBlockEntities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkLoadModule extends Module {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private float remaining;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int remainingTime;


    private int ticks;


    public ChunkLoadModule(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.CHUNK_LOAD.get(), blockPos, blockState);
    }


    @Override
    public void removed() {
        super.removed();
        if (getLevel() == null) return;
        if (getBox() == null) return;
        ServerLevel level = (ServerLevel) getLevel();
        level.setChunkForced(getBox().getPos().x,getBox().getPos().z,false);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null) return;
        if (!level.isClientSide && CConfig.get().chunkLoaderWorksEverywhere()){
            work((ServerLevel) level,pos,state,new ChunkPos(pos));
        }
        super.tick(level, pos, state);
    }

    @Override
    protected void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        if (!CConfig.get().chunkLoaderWorksEverywhere()){
            work(level,pos,state,getBox().getPos());
        }
    }

    private void work(ServerLevel level, BlockPos pos, BlockState state,ChunkPos chunkPos){
        int charges = state.getValue(ChunkLoaderModuleBlock.CHARGES);
        setRemaining(charges / 4f);
        if (charges < 1){
            level.setChunkForced(chunkPos.x,chunkPos.z,false);
            return;
        }
        ticks++;
        remainingTime = (CConfig.get().chunkLoaderChargeDuration() *20 * charges) - ticks;
        if (ticks >= CConfig.get().chunkLoaderChargeDuration() *20){
            ticks = 0;
            level.setBlockAndUpdate(pos,state.setValue(ChunkLoaderModuleBlock.CHARGES,charges - 1));
            return;
        }
        level.setChunkForced(chunkPos.x,chunkPos.z,true);
    }

    @Override
    protected void load(CompoundTag tag, HolderLookup.Provider registries) {
        super.load(tag, registries);
        if (tag.contains("remaining")) setRemaining(tag.getFloat("remaining"));
        if (tag.contains("remainingTime")) setRemainingTime(tag.getInt("remainingTime"));
    }

    @Override
    protected void save(CompoundTag tag, HolderLookup.Provider registries, boolean forClient) {
        super.save(tag, registries, forClient);
        if (forClient){
            tag.putFloat("remaining",remaining);
            tag.putInt("remainingTime",remainingTime);
        }
    }
}
