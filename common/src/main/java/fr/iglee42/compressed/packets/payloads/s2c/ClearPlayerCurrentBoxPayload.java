package fr.iglee42.compressed.packets.payloads.s2c;

import fr.iglee42.compressed.utils.Box;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressed.Compressed.MODID;


public class ClearPlayerCurrentBoxPayload implements CustomPacketPayload {

    public static final Type<ClearPlayerCurrentBoxPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "clear_player_box"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final ClearPlayerCurrentBoxPayload INSTANCE = new ClearPlayerCurrentBoxPayload();


    public static final StreamCodec<RegistryFriendlyByteBuf, ClearPlayerCurrentBoxPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}