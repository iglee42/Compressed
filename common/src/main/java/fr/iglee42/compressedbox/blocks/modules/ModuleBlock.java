package fr.iglee42.compressedbox.blocks.modules;

import fr.iglee42.compressedbox.blockentities.modules.Module;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ModuleBlock<T extends Module> extends Block implements EntityBlock {

    private final Supplier<BlockEntityType<T>> beType;

    public ModuleBlock(Properties properties, Supplier<BlockEntityType<T>> beType) {
        super(properties);
        this.beType = beType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return beType.get().create(blockPos,blockState);
    }

    @Override
    public @Nullable <B extends BlockEntity> BlockEntityTicker<B> getTicker(Level level, BlockState blockState, BlockEntityType<B> type) {
        return type.equals(beType.get()) ? (lvl,pos,state,be)->((Module)be).tick(level,pos,state) : null;
    }

    @Override
    protected void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onPlace(blockState, level, blockPos, blockState2, bl);

    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!level.isClientSide && level.dimension().equals(Box.DIMENSION) && BoxesSaveData.get(level).getBoxByBlockPos(blockPos) != null && level.getBlockEntity(blockPos) instanceof Module be)
            be.removed();
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }
}
