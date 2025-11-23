package fr.iglee42.compressed.packets.payloads.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressed.Compressed.MODID;


public class OpenClientConfigScreenPayload implements CustomPacketPayload {

    public static final Type<OpenClientConfigScreenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_client_config_screen"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final OpenClientConfigScreenPayload INSTANCE = new OpenClientConfigScreenPayload();


    public static final StreamCodec<RegistryFriendlyByteBuf, OpenClientConfigScreenPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}