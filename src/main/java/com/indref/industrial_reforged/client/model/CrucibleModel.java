package com.indref.industrial_reforged.client.model;

import com.indref.industrial_reforged.IndustrialReforged;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class CrucibleModel extends Model {
    public static final Material CRUCIBLE_LOCATION = new Material(
            InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(IndustrialReforged.MODID, "entity/crucible")
    );
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(IndustrialReforged.MODID, "crucible"), "main");
    public final ModelPart crucible;
    public final ModelPart leg0;
    public final ModelPart leg1;
    private float rotation;

    public CrucibleModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.crucible = root.getChild("crucible");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(62, 72).addBox(26.0F, -41.0F, -7.0F, 4.0F, 11.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 128).addBox(26.0F, -30.0F, -7.0F, 4.0F, 34.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(26.0F, -41.0F, 4.0F, 4.0F, 11.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(22, 0).addBox(26.0F, -41.0F, -4.0F, 4.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(48, 72).addBox(26.0F, -41.0F, -7.0F, 4.0F, 15.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(26.0F, -30.0F, -7.0F, 4.0F, 34.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(24, 66).addBox(26.0F, -41.0F, 4.0F, 4.0F, 15.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 66).addBox(26.0F, -41.0F, -4.0F, 4.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-56.0F, 20.0F, 0.0F));

        partdefinition.addOrReplaceChild("crucible", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, -6.0F, -24.0F, 48.0F, 2.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(96, 50).addBox(-24.0F, -44.0F, 20.0F, 48.0F, 38.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(136, 92).addBox(-24.0F, -40.0F, -24.0F, 48.0F, 34.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 19).addBox(3.0F, -44.0F, -28.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 11).addBox(-5.0F, -44.0F, -28.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 66).addBox(-5.0F, -40.0F, -28.0F, 10.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(136, 138).addBox(-24.0F, -44.0F, -24.0F, 21.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(136, 130).addBox(3.0F, -44.0F, -24.0F, 21.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 88).addBox(20.0F, -44.0F, -20.0F, 4.0F, 38.0F, 40.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(-24.0F, -44.0F, -20.0F, 4.0F, 38.0F, 40.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(-32.0F, -38.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(48, 50).addBox(24.0F, -38.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        renderCrucibleBody(poseStack, buffer, packedLight, packedOverlay, color);
        renderCrucibleLegs(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public void renderCrucibleBody(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0.325, 0.5);
            this.crucible.render(poseStack, buffer, packedLight, packedOverlay, color);
        }
        poseStack.popPose();
    }

    public void renderCrucibleLegs(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        {
            poseStack.translate(0.5, -1, 0.5);
            this.leg0.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.leg1.render(poseStack, buffer, packedLight, packedOverlay, color);
        }
        poseStack.popPose();
    }

    public void setupAnimation(float time) {
        this.crucible.xRot = (float) Math.toRadians(180);
        this.leg0.xRot = (float) Math.toRadians(180);
        this.leg1.xRot = (float) Math.toRadians(180);
    }

}
