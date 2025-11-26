package fr.iglee42.compressedbox.utils;

import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfig;

import java.nio.file.Path;
import java.util.List;

public interface PlatformHelper {

    CConfig getConfig();

    CClientConfig getClientConfig();

    Path getGameDir();

    String getPlatform();

    String getPlatformVersion();

    List<String> getModLoaded();

}
