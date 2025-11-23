package fr.iglee42.compressed.fabric.config;

import fr.iglee42.compressed.Compressed;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = Compressed.MODID)
@Config(name = Compressed.MODID,wrapperName = "CompressedConfig")
public class CompressedConfigModel{

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 1, max = Integer.MAX_VALUE,decimalPlaces = 0)
    public int chunkLoaderChargeDuration = 3600;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean chunkLoaderWorksEverywhere = false;
}
