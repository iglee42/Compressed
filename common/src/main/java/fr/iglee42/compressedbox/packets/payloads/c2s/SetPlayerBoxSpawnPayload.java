package fr.iglee42.compressedbox.packets.payloads.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressedbox.CompressedBox.MODID;


public class SetPlayerBoxSpawnPayload implements CustomPacketPayload {

    public static final Type<SetPlayerBoxSpawnPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "set_player_box_spawn"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final SetPlayerBoxSpawnPayload INSTANCE = new SetPlayerBoxSpawnPayload();

    public static final StreamCodec<RegistryFriendlyByteBuf, SetPlayerBoxSpawnPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}