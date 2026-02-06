package fr.iglee42.compressedbox.blocks;

import fr.iglee42.compressedbox.registries.CBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public class WallBlock extends Block {

    public static final IntegerProperty FORM = IntegerProperty.create("form",0,1);

    public WallBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FORM,0));
    }

    public static BlockState getState(int form){
        if (FORM.getPossibleValues().stream().noneMatch(v->v == form)) return CBlocks.WALL.get().defaultBlockState();
        return CBlocks.WALL.get().defaultBlockState().setValue(FORM,form);
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return blockState.getValue(FORM) == 1 ? RenderShape.INVISIBLE : super.getRenderShape(blockState);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return super.getStateForPlacement(blockPlaceContext).setValue(FORM,0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORM);
    }

    @Override
    protected float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getValue(FORM) == 1 ? 1f : super.getShadeBrightness(blockState, blockGetter, blockPos);
    }
    @Override
    protected boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getValue(FORM) == 1;
    }

}
