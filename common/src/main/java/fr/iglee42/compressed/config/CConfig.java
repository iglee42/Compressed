package fr.iglee42.compressed.config;

import fr.iglee42.compressed.utils.Services;

public interface CConfig {

    static CConfig get(){
        return Services.PLATFORM.getConfig();
    }


    int chunkLoaderChargeDuration();

    boolean chunkLoaderWorksEverywhere();

}
