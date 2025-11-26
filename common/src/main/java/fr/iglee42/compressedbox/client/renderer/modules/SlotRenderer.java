package fr.iglee42.compressedbox.client.renderer.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.iglee42.compressedbox.blockentities.modules.SlotModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SlotRenderer implements BlockEntityRenderer<SlotModule> {

    public SlotRenderer(BlockEntityRendererProvider.Context context) { }
    @Override
    public void render(SlotModule be, float f, PoseStack poseStack, MultiBufferSource buffer, int i, int j) {
        ItemStack stack = be.getHandler().getItem(0);
        Minecraft minecraft = Minecraft.getInstance();

        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float scale = stack.getItem() instanceof BlockItem ? 0.95F : 0.75F;
            poseStack.scale(scale, scale, scale);
            double tick = System.currentTimeMillis() / 800.0D;
            poseStack.translate(0.0D, Math.sin(tick % (2 * Math.PI)) * 0.065D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
            minecraft.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, LightTexture.FULL_BLOCK, j, poseStack, buffer,be.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
