package fr.iglee42.compressed.blocks.modules;

import fr.iglee42.compressed.blockentities.modules.ChunkLoadModule;
import fr.iglee42.compressed.blockentities.modules.InfiniteChunkLoadModule;
import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.registries.CBlockEntities;
import fr.iglee42.compressed.utils.Box;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class InfiniteChunkLoaderModuleBlock extends ModuleBlock<InfiniteChunkLoadModule> {

    public InfiniteChunkLoaderModuleBlock(Properties properties) {
        super(properties, CBlockEntities.INFINITE_CHUNK_LOAD);
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!level.isClientSide){
            ChunkPos pos = new ChunkPos(blockPos);
            ((ServerLevel)level).setChunkForced(pos.x,pos.z,false);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }
}
