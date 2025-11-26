package fr.iglee42.compressedbox.neoforge;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.neoforge.client.CompressedClientConfigNeoForge;
import fr.iglee42.compressedbox.utils.PlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompressedPlatformHelperNeoForge implements PlatformHelper {


    @Override
    public CConfig getConfig() {
        return CompressedConfigNeoForge.WRAPPED;
    }

    @Override
    public CClientConfig getClientConfig() {
        return FMLEnvironment.dist == Dist.CLIENT ? CompressedClientConfigNeoForge.WRAPPED : null;
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public String getPlatform() {
        return "NeoForge";
    }

    @Override
    public String getPlatformVersion() {
        return NeoForgeVersion.getVersion();
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
