package fr.iglee42.compressedbox.client.renderer.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.blockentities.modules.TankModule;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import fr.iglee42.compressedbox.utils.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

public class TankRenderer implements BlockEntityRenderer<TankModule> {

    public TankRenderer(BlockEntityRendererProvider.Context context) { }
    @Override
    public void render(TankModule be, float f, PoseStack poseStack, MultiBufferSource buffer, int i, int j) {
        FluidStack stack = be.getHandler().getFluid();
        Minecraft minecraft = Minecraft.getInstance();

        if (!stack.isEmpty()) {
            poseStack.pushPose();
            float fill = (float) stack.getAmount() / SimpleFluidContainer.MAX_FLUID;
            if (fill <= 0) return;
            AABB box = new AABB(
                    1/16D,
                    1/16D,
                    1/16D,
                    15/16D,
                    1/16D + fill * (14/16D),
                    15/16D
            );


            renderFluid(stack,box,poseStack,buffer,LightTexture.FULL_BRIGHT,be.getLevel(),be.getBlockPos(), Services.PLATFORM.getFluidSprite(stack),Services.PLATFORM.getFluidColor(stack,be.getLevel(),be.getBlockPos()));
            poseStack.popPose();
        }
    }

    public static void renderFluid(FluidStack stack,
                                   AABB box,
                                   PoseStack poseStack,
                                   MultiBufferSource buffer,
                                   int packedLight, BlockAndTintGetter getter, BlockPos pos,TextureAtlasSprite sprite, int color) {

        Fluid fluid = (Fluid) stack.getFluid(); // cast/unwrap selon ta version
        if (fluid == Fluids.EMPTY) return;

        RenderType renderLayer = ItemBlockRenderTypes.getRenderLayer(stack.getFluid().defaultFluidState());
        VertexConsumer vertexConsumer = buffer.getBuffer(renderLayer);

        float y1 = (float) box.minY;
        float y2 = (float) box.maxY;

        float minU = sprite.getU((float) box.minX);
        float maxU = sprite.getU((float) box.maxX);
        float minV = sprite.getV(y1);
        float maxV = sprite.getV(y2);

        PoseStack.Pose entry = poseStack.last();
        int overlay = OverlayTexture.NO_OVERLAY;

        // front face
        drawQuad(vertexConsumer, entry, (float) box.minX, y1, (float) box.minZ, (float) box.maxX, y2, (float) box.minZ, minU, minV, maxU, maxV, color, packedLight, overlay);

        // back face
        drawQuad(vertexConsumer, entry, (float) box.maxX, y1, (float) box.maxZ, (float) box.minX, y2, (float) box.maxZ, minU, minV, maxU, maxV, color, packedLight, overlay);

        // left face
        drawQuad(vertexConsumer, entry, (float) box.minX, y1, (float) box.maxZ, (float) box.minX, y2, (float) box.minZ, minU, minV, maxU, maxV, color, packedLight, overlay);

        // right face
        drawQuad(vertexConsumer, entry, (float) box.maxX, y1, (float) box.minZ, (float) box.maxX, y2, (float) box.maxZ, minU, minV, maxU, maxV, color, packedLight, overlay);

        minU = sprite.getU((float) box.minX);
        maxU = sprite.getU((float) box.maxX);
        minV = sprite.getV((float) box.minZ);
        maxV = sprite.getV((float) box.maxZ);

        //BOTTOM

        vertexConsumer.addVertex(entry, (float) box.minX, y1, (float) box.maxZ)
                .setColor(color)
                .setUv(minU, minV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, (float) box.minX, y1, (float) box.minZ)
                .setColor(color)
                .setUv(minU, maxV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);



        vertexConsumer.addVertex(entry, (float) box.maxX, y1, (float) box.minZ)
                .setColor(color)
                .setUv(maxU, maxV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, (float) box.maxX, y1, (float) box.maxZ)
                .setColor(color)
                .setUv(maxU, minV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);
        //TOP

        vertexConsumer.addVertex(entry, (float) box.minX, y2, (float) box.minZ)
                .setColor(color)
                .setUv(minU, maxV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, (float) box.minX, y2, (float) box.maxZ)
                .setColor(color)
                .setUv(minU, minV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, (float) box.maxX, y2, (float) box.maxZ)
                .setColor(color)
                .setUv(maxU, minV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, (float) box.maxX, y2, (float) box.minZ)
                .setColor(color)
                .setUv(maxU, maxV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static void drawQuad(VertexConsumer vertexConsumer,
                                 PoseStack.Pose entry,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float minU, float minV,
                                 float maxU, float maxV,
                                 int color,
                                 int packedLight, int overlay) {
        vertexConsumer.addVertex(entry, x1, y1, z1)
                .setColor(color)
                .setUv(minU, minV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, x1, y2, z1)
                .setColor(color)
                .setUv(minU, maxV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, x2, y2, z2)
                .setColor(color)
                .setUv(maxU, maxV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);

        vertexConsumer.addVertex(entry, x2, y1, z2)
                .setColor(color)
                .setUv(maxU, minV)
                .setLight(packedLight)
                .setOverlay(overlay)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}
