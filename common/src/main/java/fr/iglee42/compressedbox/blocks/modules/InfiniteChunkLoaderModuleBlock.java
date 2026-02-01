package fr.iglee42.compressedbox.blocks.modules;

import dev.architectury.event.EventResult;
import fr.iglee42.compressedbox.blockentities.modules.InfiniteChunkLoadModule;
import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.registries.CBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class InfiniteChunkLoaderModuleBlock extends ModuleBlock<InfiniteChunkLoadModule> {

    public InfiniteChunkLoaderModuleBlock(Properties properties) {
        super(properties, CBlockEntities.INFINITE_CHUNK_LOAD);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!level.isClientSide){
            ChunkPos pos = new ChunkPos(blockPos);
            ((ServerLevel)level).setChunkForced(pos.x,pos.z,false);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    public static EventResult craft(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        if (player.level().isClientSide) {
            return EventResult.pass();
        }
        if (!CConfig.get().enableInfiniteChunkLoaderRecipe()) return EventResult.pass();
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.is(CBlocks.CHUNK_LOADER.get().asItem())) return EventResult.pass();
        Level level = player.level();
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.BEACON)) return EventResult.pass();
        if (!(level.getBlockEntity(pos) instanceof BeaconBlockEntity be)) return EventResult.pass();
        if (be.getBeamSections().isEmpty()) return EventResult.pass();
        player.addItem(CBlocks.INFINITE_CHUNK_LOADER.get().asItem().getDefaultInstance());
        ((ServerLevel)level).sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 1D, 0.5, 0.01);
        player.playSound(SoundEvents.ITEM_PICKUP,0.75f,1);
        if (!player.isCreative())
            heldItem.shrink(1);

        if (player.level().random.nextFloat() <= CConfig.get().beaconDestroyChance()){
            level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
            level.destroyBlock(pos, false, player, Block.UPDATE_ALL);
        }

        return EventResult.interruptTrue();
    }
}
