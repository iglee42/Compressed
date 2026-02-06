package fr.iglee42.compressedbox.forge;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.forge.client.CompressedClientConfigForge;
import fr.iglee42.compressedbox.utils.PlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompressedPlatformHelperForge implements PlatformHelper {


    @Override
    public TextureAtlasSprite getFluidSprite(FluidStack stack) {
        net.minecraftforge.fluids.FluidStack fluid = FluidStackHooksForge.toForge(stack);
        var renderProps = IClientFluidTypeExtensions.of(fluid.getFluid());
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(renderProps.getStillTexture(fluid));
    }

    @Override
    public int getFluidColor(FluidStack stack, BlockAndTintGetter getter, BlockPos pos) {
        net.minecraftforge.fluids.FluidStack fluid = FluidStackHooksForge.toForge(stack);
        var renderProps = IClientFluidTypeExtensions.of(fluid.getFluid());
        return renderProps.getTintColor();
    }

    @Override
    public CConfig getConfig() {
        return CompressedConfigForge.WRAPPED;
    }

    @Override
    public CClientConfig getClientConfig() {
        return FMLEnvironment.dist == Dist.CLIENT ? CompressedClientConfigForge.WRAPPED : null;
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public String getPlatform() {
        return "Forge";
    }

    @Override
    public String getPlatformVersion() {
        return ForgeVersion.getVersion();
    }

    @Override
    public List<String> getModLoaded() {
        ModList list = ModList.get();
        List<String> mods = new ArrayList<>();
        list.getMods().stream().filter(m->!m.getModId().equalsIgnoreCase(CompressedBox.MODID))
                .forEach(mod->mods.add(mod.getModId() + " (" + mod.getVersion().toString() + ")"));
        return mods;
    }
}
