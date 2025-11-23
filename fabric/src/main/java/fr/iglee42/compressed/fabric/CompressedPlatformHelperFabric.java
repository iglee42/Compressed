package fr.iglee42.compressed.fabric;

import dev.architectury.platform.Platform;
import fr.iglee42.compressed.config.CClientConfig;
import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.fabric.client.CompressedFabricClient;
import fr.iglee42.compressed.fabric.client.CompressedOWOClientConfigWrapper;
import fr.iglee42.compressed.fabric.config.CompressedOWOConfigWrapper;
import fr.iglee42.compressed.utils.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class CompressedPlatformHelperFabric implements PlatformHelper {
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
}
