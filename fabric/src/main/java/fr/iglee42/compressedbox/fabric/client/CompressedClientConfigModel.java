package fr.iglee42.compressedbox.fabric.client;

import fr.iglee42.compressedbox.CompressedBox;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = CompressedBox.MODID)
@Config(name = CompressedBox.MODID+"-client",wrapperName = "CompressedClientConfig")
public class CompressedClientConfigModel {


    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean boxDisplayName = true;
    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean boxDisplayPreview = true;
    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean alwaysDisplayBoxInformation = true;

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean chunkLoaderDisplayBeaconBeam = true;
    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean chunkLoaderDisplayTime = true;


}
