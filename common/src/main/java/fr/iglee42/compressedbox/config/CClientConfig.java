package fr.iglee42.compressedbox.config;

import fr.iglee42.compressedbox.utils.Services;

public interface CClientConfig {

    static CClientConfig get(){
        return Services.PLATFORM.getClientConfig();
    }

    boolean displayBoxName();
    boolean displayBoxPreview();
    boolean alwaysDisplayBoxInformation();
    boolean chunkLoaderDisplayBeaconBeam();
    boolean chunkLoaderDisplayTime();

    void setDisplayBoxName(boolean value);
    void setDisplayBoxPreview(boolean value);
    void setAlwaysDisplayBoxInformation(boolean value);
    void setChunkLoaderDisplayBeaconBeam(boolean value);
    void setChunkLoaderDisplayTime(boolean value);

    default void save(){};

}
