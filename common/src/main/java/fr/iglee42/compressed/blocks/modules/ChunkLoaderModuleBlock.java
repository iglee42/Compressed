package fr.iglee42.compressed.blocks.modules;

import fr.iglee42.compressed.blockentities.modules.ChunkLoadModule;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ChunkLoaderModuleBlock extends ModuleBlock<ChunkLoadModule> {

    public static final IntegerProperty CHARGES = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;

    public ChunkLoaderModuleBlock(Properties properties) {
        super(properties, CBlockEntities.CHUNK_LOAD);
        registerDefaultState(defaultBlockState().setValue(CHARGES,0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CHARGES));
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (isFuel(itemStack) && canBeCharged(level,blockState,blockPos)) {
            charge(player, level, blockPos, blockState);
            itemStack.consume(1, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return interactionHand == InteractionHand.MAIN_HAND && isFuel(player.getItemInHand(InteractionHand.OFF_HAND)) && canBeCharged(level,blockState,blockPos) ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    private static boolean isFuel(ItemStack itemStack) {
        return itemStack.is(Items.GLOWSTONE);
    }

    public static void charge(@Nullable Entity entity, Level level, BlockPos blockPos, BlockState blockState) {
        BlockState blockState2 = blockState.setValue(CHARGES, blockState.getValue(CHARGES) + 1);
        level.setBlock(blockPos, blockState2, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, blockState2));
        level.playSound(null, (double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }


    private static boolean canBeCharged(Level level,BlockState blockState, BlockPos pos) {
        if (CConfig.get().chunkLoaderWorksEverywhere()) return blockState.getValue(CHARGES) < 4;
        return blockState.getValue(CHARGES) < 4 && level.getBlockEntity(pos) instanceof ChunkLoadModule be && level.dimension().equals(Box.DIMENSION) && be.getBox() != null;
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
