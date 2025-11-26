package fr.iglee42.compressedbox.config;

import fr.iglee42.compressedbox.utils.Services;

public interface CConfig {

    static CConfig get(){
        return Services.PLATFORM.getConfig();
    }


    int chunkLoaderChargeDuration();

    boolean chunkLoaderWorksEverywhere();

    boolean enableInfiniteChunkLoaderRecipe();
    float beaconDestroyChance();

}
