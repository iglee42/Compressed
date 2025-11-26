package fr.iglee42.compressedbox;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import fr.iglee42.compressedbox.blocks.modules.InfiniteChunkLoaderModuleBlock;
import fr.iglee42.compressedbox.commands.CCommand;
import fr.iglee42.compressedbox.packets.handlers.c2s.ExitPlayerFromBoxHandler;
import fr.iglee42.compressedbox.packets.handlers.c2s.SetBoxNameHandler;
import fr.iglee42.compressedbox.packets.handlers.c2s.SetPlayerBoxSpawnHandler;
import fr.iglee42.compressedbox.packets.payloads.c2s.ExitPlayerFromBoxPayload;
import fr.iglee42.compressedbox.packets.payloads.c2s.SetBoxNamePayload;
import fr.iglee42.compressedbox.packets.payloads.c2s.SetPlayerBoxSpawnPayload;
import fr.iglee42.compressedbox.packets.payloads.s2c.*;
import fr.iglee42.compressedbox.registries.*;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.fabricmc.api.EnvType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CompressedBox {
    public static final String MODID = "compressedbox";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final Component PREFIX = Component.literal("[").withStyle(ChatFormatting.GRAY).append(Component.literal("Compressed Box").withStyle(ChatFormatting.GREEN)).append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

    public static void init() {
        LOGGER.info("Initializing " + MODID + " mod.");

        CBlocks.BLOCKS.register();
        CItems.ITEMS.register();
        CBlockEntities.BLOCK_ENTITIES.register();
        CDataComponents.DATA_COMPONENTS.register();
        CCreativeTabs.initTabs();

        registerPacketReceivers();

        TickEvent.SERVER_LEVEL_POST.register(level-> BoxesSaveData.get(level).tick(level));
        InteractionEvent.RIGHT_CLICK_BLOCK.register(InfiniteChunkLoaderModuleBlock::craft);
        CommandRegistrationEvent.EVENT.register((dispatcher,registry,selection)-> new CCommand(dispatcher));
    }

    private static void registerPacketReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ExitPlayerFromBoxPayload.TYPE,ExitPlayerFromBoxPayload.STREAM_CODEC, ExitPlayerFromBoxHandler.instance()::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SetPlayerBoxSpawnPayload.TYPE,SetPlayerBoxSpawnPayload.STREAM_CODEC, SetPlayerBoxSpawnHandler.instance()::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SetBoxNamePayload.TYPE,SetBoxNamePayload.STREAM_CODEC, SetBoxNameHandler.instance()::handle);

        if (Platform.getEnv().equals(EnvType.SERVER)) {
            NetworkManager.registerS2CPayloadType(SyncPlayerCurrentBoxPayload.TYPE, SyncPlayerCurrentBoxPayload.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(ClearPlayerCurrentBoxPayload.TYPE, ClearPlayerCurrentBoxPayload.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(OpenClientConfigScreenPayload.TYPE, OpenClientConfigScreenPayload.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(OpenTutorialScreenPayload.TYPE, OpenTutorialScreenPayload.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(CopyToClipboardPayload.TYPE, CopyToClipboardPayload.STREAM_CODEC);
        }
    }
}
