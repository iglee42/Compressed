package fr.iglee42.compressedbox.fabric;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.fabric.client.CompressedOWOClientConfigWrapper;
import fr.iglee42.compressedbox.fabric.config.CompressedOWOConfigWrapper;
import fr.iglee42.compressedbox.utils.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompressedPlatformHelperFabric implements PlatformHelper {
    @Override
    public TextureAtlasSprite getFluidSprite(FluidStack stack) {
        FluidVariant fluidVariant = FluidVariant.of(stack.getFluid());
        return FluidVariantRendering.getSprites(fluidVariant)[0];
    }

    @Override
    public int getFluidColor(FluidStack stack, BlockAndTintGetter getter, BlockPos pos) {
        FluidVariant fluidVariant = FluidVariant.of(stack.getFluid());
        return FluidVariantRendering.getColor(fluidVariant, getter, pos);
    }

    @Override
    public CConfig getConfig() {
        return CompressedOWOConfigWrapper.INSTANCE;
    }

    @Override
    public CClientConfig getClientConfig() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT) ? CompressedOWOClientConfigWrapper.INSTANCE : null;
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public String getPlatform() {
        return "Fabric";
    }

    @Override
    public String getPlatformVersion() {
        return FabricLoaderImpl.VERSION;
    }

    @Override
    public List<String> getModLoaded() {
        List<String> mods = new ArrayList<>();
        for (var mod : FabricLoader.getInstance().getAllMods()) {
            if (mod.getMetadata().getId().equalsIgnoreCase(CompressedBox.MODID)) continue;
            mods.add(mod.getMetadata().getId() + " (" + mod.getMetadata().getVersion().toString() + ")");
        }
        return mods;
    }
}
