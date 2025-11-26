package fr.iglee42.compressedbox.neoforge.client;

import fr.iglee42.compressedbox.config.CClientConfig;
import fr.iglee42.compressedbox.config.CConfigComments;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CompressedClientConfigNeoForge {


    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue boxDisplayName;
    public static final ModConfigSpec.BooleanValue boxDisplayPreview;
    public static final ModConfigSpec.BooleanValue alwaysDisplayBoxInformation;
    public static final ModConfigSpec.BooleanValue chunkLoaderDisplayBeaconBeam;
    public static final ModConfigSpec.BooleanValue chunkLoaderDisplayTime;

    static {
        BUILDER.push("CompressedBox Client Config");

        boxDisplayName = BUILDER.comment(CConfigComments.boxDisplayName).define("boxDisplayName", true);
        boxDisplayPreview = BUILDER.comment(CConfigComments.boxDisplayPreview).define("boxDisplayPreview", true);
        alwaysDisplayBoxInformation = BUILDER.comment(CConfigComments.alwaysDisplayBoxInformation).define("alwaysDisplayBoxInformation", false);
        chunkLoaderDisplayBeaconBeam = BUILDER.comment(CConfigComments.chunkLoaderDisplayBeaconBeam).define("chunkLoaderDisplayBeaconBeam", true);
        chunkLoaderDisplayTime = BUILDER.comment(CConfigComments.chunkLoaderDisplayTime).define("chunkLoaderDisplayTime", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static final CClientConfig WRAPPED = new CClientConfig() {

        @Override
        public boolean chunkLoaderDisplayBeaconBeam() {
            return chunkLoaderDisplayBeaconBeam.get();
        }

        @Override
        public boolean displayBoxName() {
            return boxDisplayName.get();
        }

        @Override
        public boolean displayBoxPreview() {
            return boxDisplayPreview.get();
        }

        @Override
        public boolean alwaysDisplayBoxInformation() {
            return alwaysDisplayBoxInformation.get();
        }

        @Override
        public boolean chunkLoaderDisplayTime() {
            return chunkLoaderDisplayTime.get();
        }

        @Override
        public void setChunkLoaderDisplayBeaconBeam(boolean value) {
            chunkLoaderDisplayBeaconBeam.set(value);
        }

        @Override
        public void setDisplayBoxName(boolean value) {
            boxDisplayName.set(value);
        }

        @Override
        public void setDisplayBoxPreview(boolean value) {
            boxDisplayPreview.set(value);
        }

        @Override
        public void setAlwaysDisplayBoxInformation(boolean value) {
            alwaysDisplayBoxInformation.set(value);
        }

        @Override
        public void setChunkLoaderDisplayTime(boolean value) {
            chunkLoaderDisplayTime.set(value);
        }

        @Override
        public void save() {
            SPEC.save();
        }
    };
}
