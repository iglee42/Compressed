package fr.iglee42.compressed.blockentities.modules;

import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.registries.CBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteChunkLoadModule extends ChunkLoadModule {
    public InfiniteChunkLoadModule(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.INFINITE_CHUNK_LOAD.get(), blockPos, blockState);
    }

    @Override
    protected void work(ServerLevel level, BlockPos pos, BlockState state, ChunkPos chunkPos) {
        setRemaining(1f);
        setRemainingTime(CConfig.get().chunkLoaderChargeDuration() *20 * 4);
        level.setChunkForced(chunkPos.x,chunkPos.z,true);
    }
}
