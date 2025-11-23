package fr.iglee42.compressed.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.compressed.Compressed;
import fr.iglee42.compressed.containers.ConnectedSlotHandler;
import lombok.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Box {

    public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(Compressed.MODID,"compressed"));

    public static final Codec<Box> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("id", UUID.randomUUID()).forGetter(Box::getId),
            Codec.STRING.fieldOf("name").forGetter(Box::getRawName),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(Box::getOwner),
            CExtraCodecs.CHUNK_POS.fieldOf("pos").forGetter(Box::getPos),
            Codec.unboundedMap(UUIDUtil.STRING_CODEC,BlockPos.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("playersEnters").forGetter(Box::getPlayersEnters)
    ).apply(instance, Box::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Box> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Box::getId,
            ByteBufCodecs.STRING_UTF8, Box::getRawName,
            UUIDUtil.STREAM_CODEC, Box::getOwner,
            CExtraCodecs.STREAM_CHUNK_POS, Box::getPos,
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, BlockPos.STREAM_CODEC), Box::getPlayersEnters,
            Box::new
    );


    @Builder.Default
    private final UUID id = UUID.randomUUID();

    private String name;
    private UUID owner;
    private final ChunkPos pos;

    @Builder.Default
    private final HashMap<UUID, BlockPos> playersEnters = new HashMap<>();


    protected void generate(Level level){
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null){
            Compressed.LOGGER.error("Unable to find the compressed level !");
            return;
        }
        BlockPos minPos = new BlockPos(getPos().getMinBlockX() - 1,63,getPos().getMinBlockZ() - 1);
        BlockPos maxPos = new BlockPos(getPos().getMaxBlockX() + 1, 80,getPos().getMaxBlockZ() + 1);

        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();

        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    boolean isOnBoundary =
                            x == minPos.getX() || x == maxPos.getX() ||
                                    y == minPos.getY() || y == maxPos.getY() ||
                                    z == minPos.getZ() || z == maxPos.getZ();

                    if (isOnBoundary) {
                        BlockPos pos = new BlockPos(x, y, z);
                        dimension.setBlock(pos, bedrock, 3);
                    }
                }
            }
        }

    }

    public void teleportPlayerIn(Player player,ServerLevel level){
        BoxesSaveData.get(level).addPlayerEntryPoint(player);

        BlockPos pos = getPlayersEnters().getOrDefault(player.getUUID(),new BlockPos(getPos().getMiddleBlockX(),64,getPos().getMiddleBlockZ()));
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null){
            player.displayClientMessage(Component.translatable("message.compressed.level_not_found").withStyle(ChatFormatting.RED),true);
            return;
        }
        player.teleportTo(dimension,pos.getX() + 0.5,pos.getY() + 0.5,pos.getZ() + 0.5,Set.of(),player.getXRot(),player.getYRot());
    }

    public void setPlayerSpawn(Player player) {
        playersEnters.put(player.getUUID(),player.blockPosition());
    }

    public ConnectedSlotHandler getItems(Level level){
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null){
            Compressed.LOGGER.error("Unable to find the compressed level for item handler !");
            return null;
        }
        return new ConnectedSlotHandler(dimension,pos);
    }

    public Component getName(){
        return Component.literal(name.replace('&','§'));
    }

    public String getRawName(){
        return name;
    }

    public StructureTemplate createServerStructureTemplate(ServerLevel level) {
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null){
            Compressed.LOGGER.error("Unable to find the compressed level for template creation !");
            return null;
        }
        return createStructureTemplate(dimension);
    }

    public StructureTemplate createStructureTemplate(Level level){
        if (!level.dimension().equals(DIMENSION)){
            throw new IllegalArgumentException("Level passed through the createStructureTemplate method must be the compressed dimension");
        }
        StructureTemplate template = new StructureTemplate();

        template.fillFromWorld(level,
                new BlockPos(pos.getMinBlockX(),64,pos.getMinBlockZ()),
                new Vec3i(16,16,16)
                ,false,Blocks.AIR);

        return template;
    }
}
