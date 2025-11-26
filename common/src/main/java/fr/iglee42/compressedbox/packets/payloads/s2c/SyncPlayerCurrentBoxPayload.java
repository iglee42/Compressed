package fr.iglee42.compressedbox.packets.payloads.s2c;

import fr.iglee42.compressedbox.utils.Box;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressedbox.CompressedBox.MODID;


public record SyncPlayerCurrentBoxPayload(Box box) implements CustomPacketPayload {

    public static final Type<SyncPlayerCurrentBoxPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "sync_player_box"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerCurrentBoxPayload> STREAM_CODEC = StreamCodec.composite(
            Box.STREAM_CODEC,SyncPlayerCurrentBoxPayload::box,
            SyncPlayerCurrentBoxPayload::new
    );
}