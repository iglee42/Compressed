package fr.iglee42.compressedbox.blocks.modules;

import fr.iglee42.compressedbox.blockentities.modules.SlotModule;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SlotModuleBlock extends ModuleBlock<SlotModule> {
    public SlotModuleBlock(BlockBehaviour.Properties props) {
        super(props,CBlockEntities.SLOT);
    }

    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }
}
