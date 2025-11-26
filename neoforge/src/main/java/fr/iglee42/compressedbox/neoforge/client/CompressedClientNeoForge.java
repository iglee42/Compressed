package fr.iglee42.compressedbox.neoforge.client;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.client.BoxHud;
import fr.iglee42.compressedbox.client.CompressedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = CompressedBox.MODID, value = Dist.CLIENT)
public class CompressedClientNeoForge {

    public static void init(){
        CompressedClient.init();
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event){
        event.register(CompressedClient.SHOW_BOX_HUD);
    }

    @SubscribeEvent
    public static void addRenderLayers(RegisterGuiLayersEvent event){
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(CompressedBox.MODID,"box_hud"), BoxHud.HUD);
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
        if (!CompressedClient.SHOW_BOX_HUD.isDown()) return;
        int delta = (int) (event.getScrollDeltaY() == 0 ? event.getScrollDeltaX() : event.getScrollDeltaY());
        BoxHud.updateSelectedSlot(delta);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onUseClicked(InputEvent.InteractionKeyMappingTriggered event){
        if (event.getKeyMapping().equals(Minecraft.getInstance().options.keyUse) && CompressedClient.SHOW_BOX_HUD.isDown()) {
            if (BoxHud.executeSelectedAction()){
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        CompressedClient.registerBER();
    }
}
