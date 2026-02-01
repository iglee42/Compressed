package fr.iglee42.compressedbox.forge.client;

import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.client.BoxHud;
import fr.iglee42.compressedbox.client.CompressedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompressedBox.MODID, value = Dist.CLIENT)
public class CompressedClientForge {

    public static void init(){
        CompressedClient.init();
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event){
        event.register(CompressedClient.SHOW_BOX_HUD);
    }

    @SubscribeEvent
    public static void addRenderLayers(RegisterGuiOverlaysEvent event){
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), ResourceLocation.fromNamespaceAndPath(CompressedBox.MODID, "box_hud").toString(), (forgeGui, graphics, f, i, j) -> BoxHud.HUD.accept(graphics));
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
        if (!CompressedClient.SHOW_BOX_HUD.isDown()) return;
        int delta = (int) (event.getScrollDelta());
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
