package fr.iglee42.compressed.neoforge;

import fr.iglee42.compressed.config.CConfig;
import fr.iglee42.compressed.config.CConfigComments;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CompressedConfigNeoForge {


    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue chunkLoaderChargeDuration;
    public static final ModConfigSpec.BooleanValue chunkLoaderWorksEverywhere;

    static {
        BUILDER.push("Compressed Config");

        chunkLoaderChargeDuration = BUILDER.comment(CConfigComments.chunkLoaderChargeDuration).defineInRange("chunkLoaderChargeDuration", 3600,1,Integer.MAX_VALUE);
        chunkLoaderWorksEverywhere = BUILDER.comment(CConfigComments.chunkLoaderWorksEverywhere).define("chunkLoaderWorksEverywhere", false);

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
    };
}
