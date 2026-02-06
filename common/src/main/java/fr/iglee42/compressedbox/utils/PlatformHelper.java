package fr.iglee42.compressedbox.utils;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfig;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import java.nio.file.Path;
import java.util.List;

public interface PlatformHelper {


    TextureAtlasSprite getFluidSprite(FluidStack stack);

    int getFluidColor(FluidStack stack, BlockAndTintGetter getter, BlockPos pos);

    CConfig getConfig();

    CClientConfig getClientConfig();

    Path getGameDir();

    String getPlatform();

    String getPlatformVersion();

    List<String> getModLoaded();

}
