package fr.iglee42.compressedbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;

public class CExtraCodecs {

    public static final Codec<ChunkPos> CHUNK_POS = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(c->c.x),
                    Codec.INT.fieldOf("z").forGetter(c->c.z)
    ).apply(instance, ChunkPos::new));

    public static final StreamCodec<FriendlyByteBuf,ChunkPos> STREAM_CHUNK_POS = StreamCodec.composite(
            ByteBufCodecs.INT, c->c.x,
            ByteBufCodecs.INT,c->c.z,
            ChunkPos::new
    );

}
