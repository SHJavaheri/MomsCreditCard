// Made with Blockbench 4.12.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
package com.dyce.momscreditcard.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class DadEntityModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("momscreditcard", "dadentitymodel"), "main");
	private final ModelPart root;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public DadEntityModel(ModelPart root) {
		this.root = root.getChild("root");
		this.leftLeg = this.root.getChild("left_leg");
		this.rightLeg = this.root.getChild("right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root",
				CubeListBuilder.create()
							   .texOffs(0, 0).addBox(-4.0F, -32.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
							   .texOffs(16, 16).addBox(-4.0F, -24.0F, -1.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
							   .texOffs(32, 48).addBox(4.0F, -24.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
							   .texOffs(0, 16).addBox(0.0F, -12.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
							   .texOffs(40, 16).addBox(-8.0F, -24.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
							   .texOffs(16, 48).addBox(-4.0F, -12.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		// Add left leg
		root.addOrReplaceChild("left_leg",
				CubeListBuilder.create()
							   .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.0F, 12.0F, 0.0F));

		// Add right leg
		root.addOrReplaceChild("right_leg",
				CubeListBuilder.create()
							   .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.root.xRot = headPitch * ((float) Math.PI / 180F);

		// Walking animation for legs
		this.rightLeg.xRot = Mth.cos(limbSwing * 1.0F) * 1.2F * limbSwingAmount;
		this.leftLeg.xRot = Mth.cos(limbSwing * 1.0F + (float) Math.PI) * 1.2F * limbSwingAmount;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
