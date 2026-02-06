package fr.iglee42.compressedbox;

import dev.architectury.event.events.common.*;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import fr.iglee42.compressedbox.blocks.modules.InfiniteChunkLoaderModuleBlock;
import fr.iglee42.compressedbox.commands.CCommand;
import fr.iglee42.compressedbox.registries.*;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
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
        CCreativeTabs.initTabs();
        CNetworking.register();

        TickEvent.SERVER_LEVEL_POST.register(level-> BoxesSaveData.get(level).tick(level));
        InteractionEvent.RIGHT_CLICK_BLOCK.register(InfiniteChunkLoaderModuleBlock::craft);
        CommandRegistrationEvent.EVENT.register((dispatcher,registry,selection)-> new CCommand(dispatcher));

        BlockEvent.PLACE.register((level,pos,state,player)-> BoxesSaveData.prevActionOutOfBoxes(level,player,pos));
        BlockEvent.BREAK.register((level,pos,state,player,xp)-> BoxesSaveData.prevActionOutOfBoxes(level,player,pos));
        TickEvent.PLAYER_POST.register(BoxesSaveData::exitFromDimIfPlayerOutOfBox);
    }

}
