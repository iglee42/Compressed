package fr.iglee42.compressed.fabric.client;

import fr.iglee42.compressed.Compressed;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = Compressed.MODID)
@Config(name = Compressed.MODID+"-client",wrapperName = "CompressedClientConfig")
public class CompressedClientConfigModel {


    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean boxDisplayName = true;
    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean boxDisplayPreview = true;

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean chunkLoaderDisplayBeaconBeam = true;
    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean chunkLoaderDisplayTime = true;


}
