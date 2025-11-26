package fr.iglee42.compressedbox.fabric.config;

import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.fabric.CompressedFabric;

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

    @Override
    public boolean enableInfiniteChunkLoaderRecipe() {
        return CompressedFabric.CONFIG.enableInfiniteLoaderRecipe();
    }

    @Override
    public float beaconDestroyChance() {
        return CompressedFabric.CONFIG.beaconDestroyChanceInfiniteLoaderRecipe();
    }
}
