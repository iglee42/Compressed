package fr.iglee42.compressed.packets.payloads.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressed.Compressed.MODID;


public class ExitPlayerFromBoxPayload implements CustomPacketPayload {

    public static final Type<ExitPlayerFromBoxPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "durability_sync"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final ExitPlayerFromBoxPayload INSTANCE = new ExitPlayerFromBoxPayload();

    public static final StreamCodec<RegistryFriendlyByteBuf, ExitPlayerFromBoxPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}