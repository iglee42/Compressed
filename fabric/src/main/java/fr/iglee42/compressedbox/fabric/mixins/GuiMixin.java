package fr.iglee42.compressedbox.fabric.mixins;

import com.mojang.blaze3d.platform.Window;
import fr.iglee42.compressedbox.client.BoxHud;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "render",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;",ordinal = 0,shift = At.Shift.BEFORE),locals =LocalCapture.CAPTURE_FAILSOFT)
    private void render(GuiGraphics guiGraphics, float f, CallbackInfo ci, Window window, Font font, float g, float h){
        BoxHud.HUD.accept(guiGraphics);
    }
}
