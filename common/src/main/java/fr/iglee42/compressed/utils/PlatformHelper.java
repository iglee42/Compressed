package fr.iglee42.compressed.utils;

import fr.iglee42.compressed.config.CClientConfig;
import fr.iglee42.compressed.config.CConfig;

import java.nio.file.Path;

public interface PlatformHelper {

    CConfig getConfig();

    CClientConfig getClientConfig();

    Path getGameDir();

}
