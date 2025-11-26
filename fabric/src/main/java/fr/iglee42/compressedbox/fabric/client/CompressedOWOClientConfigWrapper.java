package fr.iglee42.compressedbox.fabric.client;

import fr.iglee42.compressedbox.config.CClientConfig;

public class CompressedOWOClientConfigWrapper implements CClientConfig {

    public static final CClientConfig INSTANCE = new CompressedOWOClientConfigWrapper();

    @Override
    public boolean chunkLoaderDisplayBeaconBeam() {
        return CompressedFabricClient.CLIENT_CONFIG.chunkLoaderDisplayBeaconBeam();
    }

    @Override
    public boolean chunkLoaderDisplayTime() {
        return CompressedFabricClient.CLIENT_CONFIG.chunkLoaderDisplayTime();
    }

    @Override
    public boolean displayBoxName() {
        return CompressedFabricClient.CLIENT_CONFIG.boxDisplayName();
    }

    @Override
    public boolean displayBoxPreview() {
        return CompressedFabricClient.CLIENT_CONFIG.boxDisplayPreview();
    }

    @Override
    public boolean alwaysDisplayBoxInformation() {
        return CompressedFabricClient.CLIENT_CONFIG.alwaysDisplayBoxInformation();
    }

    @Override
    public void setChunkLoaderDisplayBeaconBeam(boolean value) {
        CompressedFabricClient.CLIENT_CONFIG.chunkLoaderDisplayBeaconBeam(value);
    }

    @Override
    public void setChunkLoaderDisplayTime(boolean value) {
        CompressedFabricClient.CLIENT_CONFIG.chunkLoaderDisplayTime(value);
    }

    @Override
    public void setDisplayBoxName(boolean value) {
        CompressedFabricClient.CLIENT_CONFIG.boxDisplayName(value);
    }

    @Override
    public void setDisplayBoxPreview(boolean value) {
        CompressedFabricClient.CLIENT_CONFIG.boxDisplayPreview(value);
    }

    @Override
    public void setAlwaysDisplayBoxInformation(boolean value) {
        CompressedFabricClient.CLIENT_CONFIG.alwaysDisplayBoxInformation(value);
    }

    @Override
    public void save() {
        CompressedFabricClient.CLIENT_CONFIG.save();
    }
}
