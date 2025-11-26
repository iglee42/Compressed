package fr.iglee42.compressedbox.blockentities.modules;

import fr.iglee42.compressedbox.blocks.modules.ChunkLoaderModuleBlock;
import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.utils.Box;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChunkLoadModule extends Module {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private float remaining;
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int remainingTime;


    private int ticks;


    public ChunkLoadModule(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.CHUNK_LOAD.get(), blockPos, blockState);
    }


    public ChunkLoadModule(BlockEntityType<? extends ChunkLoadModule> type,BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }


    @Override
    public void removed() {
        super.removed();
        if (getLevel() == null) return;
        if (getBox() == null) return;
        ServerLevel level = (ServerLevel) getLevel();
        forceLoadBox(level,getBox(),false);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null) return;
        if (!level.isClientSide && CConfig.get().chunkLoaderWorksEverywhere()){
            work((ServerLevel) level,pos,state,null);
        }
        super.tick(level, pos, state);
    }

    @Override
    protected void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        work(level,pos,state,getBox());
    }

    protected void work(ServerLevel level, BlockPos pos, BlockState state,@Nullable Box box){
        int charges = state.getValue(ChunkLoaderModuleBlock.CHARGES);
        setRemaining(charges / 4f);
        if (charges < 1){
            if (box == null){
                ChunkPos chunkPos = new ChunkPos(pos);
                level.setChunkForced(chunkPos.x,chunkPos.z,false);
            } else {
                forceLoadBox(level,box,false);
            }
            return;
        }
        ticks++;
        remainingTime = (CConfig.get().chunkLoaderChargeDuration() *20 * charges) - ticks;
        if (ticks >= CConfig.get().chunkLoaderChargeDuration() *20){
            ticks = 0;
            level.setBlockAndUpdate(pos,state.setValue(ChunkLoaderModuleBlock.CHARGES,charges - 1));
            return;
        }
        if (box == null){
            ChunkPos chunkPos = new ChunkPos(pos);
            level.setChunkForced(chunkPos.x,chunkPos.z,true);
        } else {
            forceLoadBox(level,box,true);
        }
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

    public void forceLoadBox(ServerLevel level, Box box,boolean load){
        List<ChunkPos> chunks = new ArrayList<>();
        BlockPos.betweenClosed(box.getMinPos(),box.getMaxPos()).forEach(bp->{
            if (!chunks.contains(new ChunkPos(bp))) chunks.add(new ChunkPos(bp));
        });

        chunks.forEach(c->level.setChunkForced(c.x,c.z,load));
    }
}
