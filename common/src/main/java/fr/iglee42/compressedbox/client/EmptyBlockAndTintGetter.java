package fr.iglee42.compressedbox.client;

import dev.architectury.injectables.annotations.PlatformOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyBlockAndTintGetter implements BlockAndTintGetter {

    public static final EmptyBlockAndTintGetter INSTANCE = new EmptyBlockAndTintGetter();

    @Override
    public float getShade(Direction direction, boolean bl) {
        return Minecraft.getInstance().level.getShade(direction,bl);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return Minecraft.getInstance().level.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return Minecraft.getInstance().level.getBlockTint(blockPos,colorResolver);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return Blocks.AIR.defaultBlockState().getFluidState();
    }

    @Override
    public int getHeight() {
        return Minecraft.getInstance().level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return Minecraft.getInstance().level.getMinBuildHeight();
    }
}
