package fr.iglee42.compressedbox.blockentities.modules;

import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.utils.Box;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteChunkLoadModule extends ChunkLoadModule {
    public InfiniteChunkLoadModule(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.INFINITE_CHUNK_LOAD.get(), blockPos, blockState);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        setRemaining(1f);
        setRemainingTime(CConfig.get().chunkLoaderChargeDuration() *20 * 4);
        super.tick(level, pos, state);
    }

    @Override
    protected void work(ServerLevel level, BlockPos pos, BlockState state, Box box) {
        if (box == null){
            ChunkPos chunkPos = new ChunkPos(pos);
            level.setChunkForced(chunkPos.x,chunkPos.z,true);
        } else {
            forceLoadBox(level,box,true);
        }
    }
}
