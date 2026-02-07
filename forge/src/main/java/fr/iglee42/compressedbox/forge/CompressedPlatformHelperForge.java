package fr.iglee42.compressedbox.forge;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.forge.client.CompressedClientConfigForge;
import fr.iglee42.compressedbox.utils.PlatformHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompressedPlatformHelperForge implements PlatformHelper {

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
