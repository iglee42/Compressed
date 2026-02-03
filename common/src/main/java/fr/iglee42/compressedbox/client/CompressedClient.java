package fr.iglee42.compressedbox.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.client.renderer.CompressedBERenderer;
import fr.iglee42.compressedbox.client.renderer.modules.ChunkLoadRenderer;
import fr.iglee42.compressedbox.client.renderer.modules.SlotRenderer;
import fr.iglee42.compressedbox.packets.c2s.SetBoxNamePacket;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.registries.CBlocks;
import fr.iglee42.compressedbox.registries.CNetworking;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.TutorialRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import org.lwjgl.glfw.GLFW;

public class CompressedClient {

    public static Box currentBox = null;
    protected static int nameEditCountdown = -1;

    public static final KeyMapping SHOW_BOX_HUD = new KeyMapping("keybind.compressedbox.show_hud", GLFW.GLFW_KEY_LEFT_ALT,"keybind.compressedbox.category");

    public static void init(){
        ClientTickEvent.CLIENT_POST.register(e->{
            if (nameEditCountdown > 0) nameEditCountdown--;
            if (nameEditCountdown == 0){
                if (e.player != null) e.player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.box_rename_expired").withStyle(ChatFormatting.RED)),false);
                nameEditCountdown--;
            }
        });

        ClientChatEvent.SEND.register((msg,comp)->{
            if (nameEditCountdown <= -1) return EventResult.pass();
            if (currentBox == null) return EventResult.pass();
            if (msg.trim().equalsIgnoreCase("cancel")) {
                if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.displayClientMessage(CompressedBox.PREFIX.copy().append(Component.translatable("message.compressedbox.box_rename_cancelled").withStyle(ChatFormatting.RED)),false);
                nameEditCountdown = -1;
                return EventResult.interruptFalse();
            }
            CNetworking.CHANNEL.sendToServer(new SetBoxNamePacket(currentBox.getId(),msg));
            nameEditCountdown = -1;
            return EventResult.interruptFalse();
        });
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES,new TutorialRegistry());
        if (Platform.isFabric()) RenderTypeRegistry.register(RenderType.cutoutMipped(), CBlocks.COMPRESSED_BLOCK.get());
    }

    public static void registerBER(){
        BlockEntityRendererRegistry.register(CBlockEntities.COMPRESSED.get(), CompressedBERenderer::new);
        BlockEntityRendererRegistry.register(CBlockEntities.SLOT.get(), SlotRenderer::new);
        BlockEntityRendererRegistry.register(CBlockEntities.CHUNK_LOAD.get(), ChunkLoadRenderer::new);
        BlockEntityRendererRegistry.register(CBlockEntities.INFINITE_CHUNK_LOAD.get(), ChunkLoadRenderer::new);
    }



}
