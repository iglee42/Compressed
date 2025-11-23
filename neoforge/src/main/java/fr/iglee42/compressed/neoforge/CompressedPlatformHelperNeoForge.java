package fr.iglee42.compressed.neoforge;

import fr.iglee42.compressed.config.CClientConfig;
import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.neoforge.client.CompressedClientConfigNeoForge;
import fr.iglee42.compressed.utils.PlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

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
}
