package fr.iglee42.compressed.packets.payloads.c2s;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

import static fr.iglee42.compressed.Compressed.MODID;


public record SetBoxNamePayload(UUID boxId, String name) implements CustomPacketPayload {

    public static final Type<SetBoxNamePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "set_box_name"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SetBoxNamePayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,SetBoxNamePayload::boxId,
            ByteBufCodecs.STRING_UTF8,SetBoxNamePayload::name,
            SetBoxNamePayload::new
    );
}