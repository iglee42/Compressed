package fr.iglee42.compressedbox.packets.payloads.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressedbox.CompressedBox.MODID;


public class OpenTutorialScreenPayload implements CustomPacketPayload {

    public static final Type<OpenTutorialScreenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_tutorial_screen"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final OpenTutorialScreenPayload INSTANCE = new OpenTutorialScreenPayload();


    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTutorialScreenPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}