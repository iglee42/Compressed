package fr.iglee42.compressedbox.packets.payloads.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.compressedbox.CompressedBox.MODID;


public record CopyToClipboardPayload(String toCopy) implements CustomPacketPayload {

    public static final Type<CopyToClipboardPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "copy_to_clipboard"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static final StreamCodec<RegistryFriendlyByteBuf, CopyToClipboardPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CopyToClipboardPayload::toCopy,
            CopyToClipboardPayload::new
    );
}