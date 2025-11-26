package fr.iglee42.compressedbox.client.renderer.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.iglee42.compressedbox.blockentities.modules.ChunkLoadModule;
import fr.iglee42.compressedbox.blockentities.modules.InfiniteChunkLoadModule;
import fr.iglee42.compressedbox.config.CClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ChunkLoadRenderer<T extends ChunkLoadModule> implements BlockEntityRenderer<T> {

    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    public ChunkLoadRenderer(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(T be, float pt, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (be.getRemaining() > 0 && be.getLevel() != null){
            if (CClientConfig.get().chunkLoaderDisplayBeaconBeam())
                BeaconRenderer.renderBeaconBeam(
                    poseStack,
                    buffer,
                    BEAM_LOCATION,
                    pt,
                    1.0f,
                    be.getLevel().getGameTime(),
                    0,
                    BeaconRenderer.MAX_RENDER_Y,
                    be.getRemaining() > 0.5 ? DyeColor.LIME.getTextureDiffuseColor() : (
                            be.getRemaining() > 0.25 ? DyeColor.YELLOW.getTextureDiffuseColor() : DyeColor.RED.getTextureDiffuseColor()
                    ),
                    0.2f,
                    0.25f
                );
            if (CClientConfig.get().chunkLoaderDisplayTime()){
                if (shouldRenderName(Minecraft.getInstance(),Minecraft.getInstance().player,be )){
                    renderNameTag(Minecraft.getInstance(),formatTicksToTimeComponent(be.getRemainingTime()),poseStack,buffer,light);
                }
            }
        }
    }

    protected void renderNameTag(Minecraft minecraft,  Component component, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.4f, 0.5f);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float bgOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int alpha = (int)(bgOpacity * 255.0F) << 24;
        Font font = minecraft.font;
        float offset = (float)(-font.width(component) / 2);
        font.drawInBatch(component, offset, 0, 553648127, false, matrix4f, buffer,  Font.DisplayMode.SEE_THROUGH , alpha, LightTexture.FULL_BRIGHT);
        font.drawInBatch(component, offset, 0, -1, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }


    private boolean shouldRenderName(Minecraft minecraft, Player player, T be) {
        if (be instanceof InfiniteChunkLoadModule) return false;
        double sneakRadius = 16;
        if (player.isCrouching()){
            return sneakRadius > 0 && player.position().distanceToSqr(new Vec3(be.getBlockPos().getX() + 0.5, be.getBlockPos().getY() + 0.5,be.getBlockPos().getZ() + 0.5)) <= sneakRadius * sneakRadius;
        }

        return minecraft.hitResult != null && minecraft.hitResult.getType().equals(HitResult.Type.BLOCK) && ((BlockHitResult)minecraft.hitResult).getBlockPos().equals(be.getBlockPos());
    }

    public static Component formatTicksToTimeComponent(long ticks) {
        long totalSeconds = ticks / 20;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (days > 0) sb.append(days).append("d ");
        if (hours > 0 || days > 0) sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0 || days > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return Component.literal(sb.toString().trim());
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
