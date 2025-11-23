package fr.iglee42.compressed.config;

import fr.iglee42.compressed.utils.Services;

public interface CClientConfig {

    static CClientConfig get(){
        return Services.PLATFORM.getClientConfig();
    }

    boolean displayBoxName();
    boolean displayBoxPreview();
    boolean chunkLoaderDisplayBeaconBeam();
    boolean chunkLoaderDisplayTime();

    void setDisplayBoxName(boolean value);
    void setDisplayBoxPreview(boolean value);
    void setChunkLoaderDisplayBeaconBeam(boolean value);
    void setChunkLoaderDisplayTime(boolean value);

    default void save(){};

}
