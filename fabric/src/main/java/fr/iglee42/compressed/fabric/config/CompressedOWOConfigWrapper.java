package fr.iglee42.compressed.fabric.config;

import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.fabric.CompressedFabric;

public class CompressedOWOConfigWrapper implements CConfig {

    public static final CConfig INSTANCE = new CompressedOWOConfigWrapper();

    @Override
    public int chunkLoaderChargeDuration() {
        return CompressedFabric.CONFIG.chunkLoaderChargeDuration();
    }

    @Override
    public boolean chunkLoaderWorksEverywhere() {
        return CompressedFabric.CONFIG.chunkLoaderWorksEverywhere();
    }
}
