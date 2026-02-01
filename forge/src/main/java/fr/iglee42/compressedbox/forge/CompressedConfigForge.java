package fr.iglee42.compressedbox.forge;

import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.config.CConfigComments;
import net.minecraftforge.common.ForgeConfigSpec;


public class CompressedConfigForge {


    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue chunkLoaderChargeDuration;
    public static final ForgeConfigSpec.BooleanValue chunkLoaderWorksEverywhere;
    public static final ForgeConfigSpec.BooleanValue infiniteChunkLoaderRecipeEnabled;
    public static final ForgeConfigSpec.DoubleValue beaconDestroyChance;

    static {
        BUILDER.push("CompressedBox Config");

        chunkLoaderChargeDuration = BUILDER.comment(CConfigComments.chunkLoaderChargeDuration).defineInRange("chunkLoaderChargeDuration", 3600,1,Integer.MAX_VALUE);
        chunkLoaderWorksEverywhere = BUILDER.comment(CConfigComments.chunkLoaderWorksEverywhere).define("chunkLoaderWorksEverywhere", false);
        infiniteChunkLoaderRecipeEnabled = BUILDER.comment(CConfigComments.enableInfiniteChunkLoaderRecipe).define("infiniteChunkLoaderRecipeEnabled", true);
        beaconDestroyChance = BUILDER.comment(CConfigComments.beaconDestroyChance).defineInRange("beaconDestroyChance", 0.25,0,1);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static final CConfig WRAPPED = new CConfig() {

        @Override
        public int chunkLoaderChargeDuration() {
            return chunkLoaderChargeDuration.get();
        }

        @Override
        public boolean chunkLoaderWorksEverywhere() {
            return chunkLoaderWorksEverywhere.get();
        }

        @Override
        public boolean enableInfiniteChunkLoaderRecipe() {
            return infiniteChunkLoaderRecipeEnabled.get();
        }

        @Override
        public float beaconDestroyChance() {
            return beaconDestroyChance.get().floatValue();
        }
    };
}
