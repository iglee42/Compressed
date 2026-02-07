package fr.iglee42.compressedbox.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.blocks.CompressedBlock;
import fr.iglee42.compressedbox.packets.s2c.CopyToClipboardPacket;
import fr.iglee42.compressedbox.packets.s2c.OpenClientConfigScreenPacket;
import fr.iglee42.compressedbox.registries.CBlocks;
import fr.iglee42.compressedbox.registries.CNetworking;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import fr.iglee42.compressedbox.utils.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.ChatFormatting.RED;

public class CCommand{

    public CCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(CompressedBox.MODID)
                .then(Commands.literal("config").executes(this::openConfig))
                .then(Commands.literal("debugInfos").executes(this::debugInfos))
                .then(Commands.literal("currentBox")
                        .executes(this::currentBoxInfos)
                        .then(Commands.argument("player",EntityArgument.player())
                                .requires(src->src.hasPermission(2))
                                .executes(this::currentBoxInfosForPlayer)))
                .then(Commands.literal("box")
                        .requires(src->src.hasPermission(2))
                        .then(Commands.literal("list")
                                .executes(this::listBoxes))
                        .then(Commands.argument("box", UuidArgument.uuid())
                                .executes(this::boxInfos)))
        );
    }

    private int openConfig(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        if (!source.isPlayer()){
            sendFailure(source,Component.literal("This command can only be executed by a player."));
            return 0;
        }
        CNetworking.CHANNEL.sendToPlayer(source.getPlayerOrException(), OpenClientConfigScreenPacket.INSTANCE);
        return 1;
    }

    private int debugInfos(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        if (!source.isPlayer()){
            sendFailure(source,Component.literal("This command can only be executed by a player."));
            return 0;
        }
        StringBuilder debugInfos = new StringBuilder();
        Player player = source.getPlayerOrException();
        Level level = player.level();

        BoxesSaveData manager = BoxesSaveData.get(level);

        debugInfos.append("=== CompressedBox Debug Infos ===\n\n");
        debugInfos.append("Minecraft Version: ").append(player.level().getServer().getServerVersion()).append("\n");
        debugInfos.append("Platform: ").append(Services.PLATFORM.getPlatform()).append("\n");
        debugInfos.append("Platform Version: ").append(Services.PLATFORM.getPlatformVersion()).append("\n");
        debugInfos.append("Other Mods: ").append(Services.PLATFORM.getModLoaded().stream().reduce("", (a, b) -> a + " | " + b)).append("\n");

        debugInfos.append("\n===========================\n\n");
        debugInfos.append("Player UUID: ").append(player.getUUID()).append("\n");
        debugInfos.append("Player Position: ").append(player.blockPosition()).append("\n");
        debugInfos.append("Dimension: ").append(level.dimension().location()).append("\n");
        debugInfos.append("Existing Boxes: ").append(manager.getBoxes().size()).append("\n");
        debugInfos.append("\n===========================\n\n");
        Box currentBox = manager.getBoxFromPlayer(player);
        debugInfos.append("Current Box: ").append(currentBox == null ? "None" : currentBox.getId()).append("\n");
        if (currentBox != null){
            debugInfos.append(" - Name: ").append(currentBox.getRawName()).append("\n");
            debugInfos.append(" - Owner: ").append(currentBox.getOwner()).append("\n");
            debugInfos.append(" - Minimum Position: ").append(currentBox.getMinPos()).append("\n");
            debugInfos.append(" - Maximum Position: ").append(currentBox.getMaxPos()).append("\n");
        }
        debugInfos.append("\n===========================");

        source.sendSuccess(()->Component.literal(debugInfos.toString()).append(Component.literal("\nThese information were copied to be used in a bug/crash report !").withStyle(ChatFormatting.GREEN)),false);

        CNetworking.CHANNEL.sendToPlayer(source.getPlayerOrException(),new CopyToClipboardPacket(debugInfos.toString()));
        return 1;
    }

    private int currentBoxInfos(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        if (!source.isPlayer()){
            sendFailure(source,Component.literal("This command can only be executed by a player."));
            return 0;
        }
        Player player = source.getPlayerOrException();
        Level level = player.level();
        BoxesSaveData manager = BoxesSaveData.get(level);

        Box currentBox = manager.getBoxFromPlayer(player);

        if (currentBox == null){
            sendFailure(source,Component.literal("You are not in a box."));
            return 0;
        }

        Pair<Integer,Component> out = infosForBox(currentBox,level,source.hasPermission(2));
        if (out.getFirst() == 0){
            sendFailure(source,out.getSecond());
            return 0;
        }

        source.sendSuccess(out::getSecond,false);
        return out.getFirst();
    }

    private int currentBoxInfosForPlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Player player;
        try {
             player = EntityArgument.getPlayer(ctx,"player");
        } catch (Exception e){
            sendFailure(source,Component.literal("Player not found."));
            return 0;
        }
        Level level = player.level();
        BoxesSaveData manager = BoxesSaveData.get(level);

        Box currentBox = manager.getBoxFromPlayer(player);

        if (currentBox == null){
            sendFailure(source,player.getName().copy().append(Component.literal( " is not in a box.")));
            return 0;
        }

        Pair<Integer,Component> out = infosForBox(currentBox,level,source.hasPermission(2));
        if (out.getFirst() == 0){
            sendFailure(source,out.getSecond());
            return 0;
        }

        source.sendSuccess(out::getSecond,false);
        return out.getFirst();
    }

    private int boxInfos(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        if (ctx.getArgument("box", UUID.class) == null){
            sendFailure(source,Component.literal("Box not found."));
            return 0;
        }

        UUID boxId = ctx.getArgument("box", UUID.class);
        Level level = ctx.getSource().getServer().overworld();
        BoxesSaveData manager = BoxesSaveData.get(level);

        Box currentBox = manager.getBox(boxId);

        if (currentBox == null){
            sendFailure(source,Component.literal("Box not found."));
            return 0;
        }

        Pair<Integer,Component> out = infosForBox(currentBox,level,source.hasPermission(2));
        if (out.getFirst() == 0){
            sendFailure(source,out.getSecond());
            return 0;
        }

        source.sendSuccess(out::getSecond,false);
        return out.getFirst();
    }

    private int listBoxes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Level level = ctx.getSource().getServer().overworld();
        BoxesSaveData manager = BoxesSaveData.get(level);
        MutableComponent debugInfos = Component.empty();
        debugInfos.append("=== Existing Boxes ===\n\n");
        manager.getBoxes().forEach(b->{
            debugInfos.append("Id: ").append(Component.literal(b.getId().toString()).withStyle(Style.EMPTY
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Component.literal("Click to Copy")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, b.getId().toString()))))
                    .append(" | Name: ").append(b.getRawName())
                    .append("\n");
        });
        debugInfos.append("\n===========================");

        source.sendSuccess(()->debugInfos,false);
        return 1;
    }

    private Pair<Integer,Component> infosForBox(Box currentBox, Level level, boolean op){

        MutableComponent debugInfos = Component.empty();

        debugInfos.append("=== Current Box Infos ===\n\n");
        debugInfos.append("Id: ").append(Component.literal(currentBox.getId().toString()).withStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Component.literal("Click to Copy")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, currentBox.getId().toString()))))
                .append("\n");
        debugInfos.append("Name: ").append(currentBox.getRawName()).append("\n");

        Optional<GameProfile> ownerProfile = level.getServer().getProfileCache().get(currentBox.getOwner());
        AtomicReference<String> name = new AtomicReference<>("Unknown");
        ownerProfile.ifPresent(p-> name.set(p.getName()));
        debugInfos.append("Owner: ").append(Component.literal(currentBox.getOwner().toString()).withStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Component.literal("Click to Copy")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, currentBox.getOwner().toString())))).append(Component.literal(" ( " + name + " )"))
                .append("\n");
        debugInfos.append("Minimum Position: ").append(currentBox.getMinPos().toString()).append("\n");
        debugInfos.append("Maximum Position: ").append(currentBox.getMaxPos().toString()).append("\n");
        debugInfos.append("\n===========================");

        if (op){
            debugInfos.append(Component.literal("\n[GIVE BOX]").withStyle(Style.EMPTY.
                    withBold(true)
                    .applyFormat(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/give @p " + CBlocks.COMPRESSED_BLOCK.getId().toString() + "{\"" + CompressedBlock.BOX_ID_NBT_KEY + "\":" + NbtUtils.createUUID(currentBox.getId()) + "}")))).append(" ");

            debugInfos.append(Component.literal("[TP IN BOX]").withStyle(Style.EMPTY.
                    withBold(true)
                    .applyFormat(ChatFormatting.AQUA)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/execute in "+ Box.DIMENSION.location() + " run tp @p " + currentBox.getCenterPos(64).getX() + " 64 " + currentBox.getCenterPos(64).getZ()))));
        }

        return Pair.of(1,debugInfos);
    }

    private static void sendFailure(CommandSourceStack source,Component message){
        source.sendSystemMessage(CompressedBox.PREFIX.copy().append(message.copy().withStyle(RED)));
    }

}
