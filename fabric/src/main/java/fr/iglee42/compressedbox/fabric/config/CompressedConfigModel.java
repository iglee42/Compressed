package fr.iglee42.compressedbox.fabric.config;

import fr.iglee42.compressedbox.CompressedBox;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = CompressedBox.MODID)
@Config(name = CompressedBox.MODID,wrapperName = "CompressedConfig")
public class CompressedConfigModel{

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 1, max = Integer.MAX_VALUE,decimalPlaces = 0)
    public int chunkLoaderChargeDuration = 3600;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean chunkLoaderWorksEverywhere = false;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableInfiniteLoaderRecipe = true;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 0.0f, max = 1.0f, decimalPlaces = 2)
    public float beaconDestroyChanceInfiniteLoaderRecipe = 0.25f;
}
