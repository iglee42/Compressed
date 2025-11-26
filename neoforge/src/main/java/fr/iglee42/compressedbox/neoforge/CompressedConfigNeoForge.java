package fr.iglee42.compressedbox.neoforge;

import fr.iglee42.compressedbox.config.CConfig;
import fr.iglee42.compressedbox.config.CConfigComments;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CompressedConfigNeoForge {


    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue chunkLoaderChargeDuration;
    public static final ModConfigSpec.BooleanValue chunkLoaderWorksEverywhere;
    public static final ModConfigSpec.BooleanValue infiniteChunkLoaderRecipeEnabled;
    public static final ModConfigSpec.DoubleValue beaconDestroyChance;

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
