package fr.iglee42.compressedbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.blocks.WallBlock;
import fr.iglee42.compressedbox.containers.ConnectedSlotHandler;
import fr.iglee42.compressedbox.containers.fluids.ConnectedTankHandler;
import fr.iglee42.compressedbox.packets.payloads.s2c.OpenTutorialScreenPayload;
import lombok.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Box {
    
    public static final int MAX_BOX_SIZE = 32;

    public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(CompressedBox.MODID, "compressed"));

    public static final Codec<Box> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("id", UUID.randomUUID()).forGetter(Box::getId),
                    Codec.STRING.fieldOf("name").forGetter(Box::getRawName),
                    UUIDUtil.CODEC.fieldOf("owner").forGetter(Box::getOwner),
                    BlockPos.CODEC.fieldOf("minPos").forGetter(Box::getMinPos),
                    BlockPos.CODEC.fieldOf("maxPos").forGetter(Box::getMaxPos),
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, BlockPos.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("playersEnters").forGetter(Box::getPlayersEnters))
                    .apply(instance, Box::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Box> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Box::getId,
            ByteBufCodecs.STRING_UTF8, Box::getRawName,
            UUIDUtil.STREAM_CODEC, Box::getOwner,
            BlockPos.STREAM_CODEC, Box::getMinPos,
            BlockPos.STREAM_CODEC, Box::getMaxPos,
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, BlockPos.STREAM_CODEC), Box::getPlayersEnters,
            Box::new);




    @Builder.Default
    private final UUID id = UUID.randomUUID();
    private String name;
    private UUID owner;
    private BlockPos minPos;
    private BlockPos maxPos;
    @Builder.Default
    private final HashMap<UUID, BlockPos> playersEnters = new HashMap<>();

    protected void generate(Level level) {
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            CompressedBox.LOGGER.error("Unable to find the compressed level !");
            return;
        }
        BlockPos minPos = getMinPos().offset(-1, -1, -1); // 63 + 1
        BlockPos maxPos = getMaxPos().offset(1, 1, 1); //80 - 1


        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    boolean onX = x == minPos.getX() || x == maxPos.getX();
                    boolean onY = y == minPos.getY() || y == maxPos.getY();
                    boolean onZ = z == minPos.getZ() || z == maxPos.getZ();

                    int boundaryCount = (onX ? 1 : 0) + (onY ? 1 : 0) + (onZ ? 1 : 0);

                    boolean isOnBoundary = boundaryCount >= 1;
                    boolean isOnEdge = boundaryCount == 2;
                    boolean isOnCorner = boundaryCount == 3;

                    if (isOnBoundary) {
                        BlockPos pos = new BlockPos(x, y, z);
                        dimension.setBlock(pos, WallBlock.getState( isOnEdge || isOnCorner ? 0 : 1), 3);
                    }
                }
            }
        }

    }

    public void teleportPlayerIn(Player player, ServerLevel level) {
        BoxesSaveData manager = BoxesSaveData.get(level);
        if (!player.level().dimension().equals(DIMENSION)) manager.addPlayerEntryPoint(player);

        BlockPos pos = getPlayersEnters().getOrDefault(player.getUUID(), new BlockPos(minPos.getX() + 1, 64, minPos.getZ() + 1));
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            player.displayClientMessage(Component.translatable("message.compressedbox.level_not_found").withStyle(ChatFormatting.RED), true);
            return;
        }
        player.teleportTo(dimension, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Set.of(), player.getXRot(), player.getYRot());

        if (manager.isPlayerNew(player)) {
            NetworkManager.sendToPlayer((ServerPlayer) player, OpenTutorialScreenPayload.INSTANCE);
            manager.registerKnownPlayer(player);
        }
    }

    public void setPlayerSpawn(Player player) {
        playersEnters.put(player.getUUID(), player.blockPosition());
    }

    public ConnectedSlotHandler getItems(Level level) {
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            CompressedBox.LOGGER.error("Unable to find the compressed level for item handler !");
            return null;
        }
        return new ConnectedSlotHandler(dimension, minPos, maxPos);
    }

    public ConnectedTankHandler getFluids(Level level) {
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            CompressedBox.LOGGER.error("Unable to find the compressed level for fluid handler !");
            return null;
        }
        return new ConnectedTankHandler(dimension, minPos, maxPos);
    }

    public Component getName() {
        return Component.literal(name.replace('&', '§'));
    }

    public String getRawName() {
        return name;
    }

    public StructureTemplate createServerStructureTemplate(ServerLevel level) {
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            CompressedBox.LOGGER.error("Unable to find the compressed level for template creation !");
            return null;
        }
        return createStructureTemplate(dimension);
    }

    public StructureTemplate createStructureTemplate(Level level) {
        if (!level.dimension().equals(DIMENSION)) {
            throw new IllegalArgumentException("Level passed through the createStructureTemplate method must be the compressed dimension");
        }
        StructureTemplate template = new StructureTemplate();

        template.fillFromWorld(level, minPos, new BlockPos(maxPos.getX() - minPos.getX() + 1, maxPos.getY() - minPos.getY() + 1, maxPos.getZ() - minPos.getZ() + 1), false, Blocks.AIR);

        return template;
    }

    public BlockPos getCenterPos(int y) {
        int centerX = (minPos.getX() + maxPos.getX()) / 2;
        int centerZ = (minPos.getZ() + maxPos.getZ()) / 2;
        return new BlockPos(centerX, y, centerZ);
    }


    public boolean pushWall(Direction face, ServerLevel dimension) {
        if (dimension == null || !dimension.dimension().equals(DIMENSION)) {
            CompressedBox.LOGGER.error("Invalid dimension passed to pushWall");
            return false;
        }

        BlockPos minDestroyPos = getMinPos().offset(-1, -1, -1); // 63 + 1
        BlockPos maxDestroyPos = getMaxPos().offset(1, 1, 1); //80 - 1

        boolean success = false;


        if (face.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            BlockPos newMax = maxPos.relative(face);
            if (newMax.getX() - minPos.getX() < MAX_BOX_SIZE && newMax.getY() - minPos.getY() < MAX_BOX_SIZE && newMax.getZ() - minPos.getZ() < MAX_BOX_SIZE) {
                setMaxPos(newMax);
                success = true;
            }
        } else {
            BlockPos newMin = minPos.relative(face);
            if (maxPos.getX() - newMin.getX() < MAX_BOX_SIZE && maxPos.getY() - newMin.getY() < MAX_BOX_SIZE && maxPos.getZ() - newMin.getZ() < MAX_BOX_SIZE) {
                setMinPos(newMin);
                success = true;
            }
        }

        if (success){
            BlockState air = Blocks.AIR.defaultBlockState();

            for (int x = minDestroyPos.getX(); x <= maxDestroyPos.getX(); x++) {
                for (int y = minDestroyPos.getY(); y <= maxDestroyPos.getY(); y++) {
                    for (int z = minDestroyPos.getZ(); z <= maxDestroyPos.getZ(); z++) {
                        boolean isOnBoundary = x == minDestroyPos.getX() || x == maxDestroyPos.getX() || y == minDestroyPos.getY() || y == maxDestroyPos.getY() || z == minDestroyPos.getZ() || z == maxDestroyPos.getZ();

                        if (isOnBoundary) {
                            BlockPos pos = new BlockPos(x, y, z);
                            dimension.setBlock(pos, air, 3);
                        }
                    }
                }
            }
            generate(dimension);
        }

        return success;
    }
}
