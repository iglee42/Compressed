package fr.iglee42.compressedbox.utils;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.containers.ConnectedSlotHandler;
import fr.iglee42.compressedbox.packets.s2c.OpenTutorialScreenPacket;
import fr.iglee42.compressedbox.registries.CNetworking;
import lombok.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Box {
    
    public static final int MAX_BOX_SIZE = 32;

    public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(CompressedBox.MODID, "compressed"));

    @Builder.Default
    private final UUID id = UUID.randomUUID();
    private String name;
    private UUID owner;
    private BlockPos minPos;
    private BlockPos maxPos;
    @Builder.Default
    private final HashMap<UUID, BlockPos> playersEnters = new HashMap<>();

    public static Box decode(FriendlyByteBuf buffer){
        return new Box(buffer.readUUID(),buffer.readUtf(),buffer.readUUID(),buffer.readBlockPos(),buffer.readBlockPos(),new HashMap<>(buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readBlockPos)));
    }

    public static Box load(CompoundTag tag){
        Map<UUID,BlockPos> playersEnters = new HashMap<>();
        ListTag entries = tag.getList("playersEnters", CompoundTag.TAG_COMPOUND);
        entries.stream().map(CompoundTag.class::cast).forEach(enter->{
            playersEnters.put(enter.getUUID("player"), NbtUtils.readBlockPos(enter.getCompound("pos")));
        });
        return new Box(tag.getUUID("id"), tag.getString("name"), tag.getUUID("owner"), NbtUtils.readBlockPos(tag.getCompound("minPos")),NbtUtils.readBlockPos(tag.getCompound("maxPos")),new HashMap<>(playersEnters));
    }



    protected void generate(Level level) {
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            CompressedBox.LOGGER.error("Unable to find the compressed level !");
            return;
        }
        BlockPos minPos = getMinPos().offset(-1, -1, -1); // 63 + 1
        BlockPos maxPos = getMaxPos().offset(1, 1, 1); //80 - 1

        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();

        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    boolean isOnBoundary = x == minPos.getX() || x == maxPos.getX() || y == minPos.getY() || y == maxPos.getY() || z == minPos.getZ() || z == maxPos.getZ();

                    if (isOnBoundary) {
                        BlockPos pos = new BlockPos(x, y, z);
                        dimension.setBlock(pos, bedrock, 3);
                    }
                }
            }
        }

    }


    public void encode(FriendlyByteBuf buffer){
        buffer.writeUUID(getId());
        buffer.writeUtf(getRawName());
        buffer.writeUUID(getOwner());
        buffer.writeBlockPos(getMinPos());
        buffer.writeBlockPos(getMaxPos());
        buffer.writeMap(getPlayersEnters(), FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeBlockPos);
    }

    public CompoundTag save(){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id",getId());
        tag.putString("name",getRawName());
        tag.putUUID("owner",getOwner());
        tag.put("minPos",NbtUtils.writeBlockPos(getMinPos()));
        tag.put("maxPos",NbtUtils.writeBlockPos(getMaxPos()));

        ListTag entries = new ListTag();
        getPlayersEnters().forEach((uuid,pos)->{
            CompoundTag enter = new CompoundTag();
            enter.putUUID("player",uuid);
            enter.put("pos",NbtUtils.writeBlockPos(pos));
            entries.add(enter);
        });
        tag.put("playersEnters",entries);
        return tag;
    }


    public void teleportPlayerIn(Player player, ServerLevel level) {
        BoxesSaveData manager = BoxesSaveData.get(level);
        manager.addPlayerEntryPoint(player);

        BlockPos pos = getPlayersEnters().getOrDefault(player.getUUID(), new BlockPos(minPos.getX() + 1, 64, minPos.getZ() + 1));
        ServerLevel dimension = level.getServer().getLevel(DIMENSION);
        if (dimension == null) {
            player.displayClientMessage(Component.translatable("message.compressedbox.level_not_found").withStyle(ChatFormatting.RED), true);
            return;
        }
        player.teleportTo(dimension, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Set.of(), player.getXRot(), player.getYRot());

        if (manager.isPlayerNew(player)) {
            CNetworking.CHANNEL.sendToPlayer((ServerPlayer) player, OpenTutorialScreenPacket.INSTANCE);
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
