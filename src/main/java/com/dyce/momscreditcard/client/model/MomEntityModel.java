package com.dyce.momscreditcard.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MomEntityModel<T extends Entity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("momscreditcard", "mom"), "main");

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public MomEntityModel(ModelPart modelPart) {
		this.root = modelPart;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root",
				CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0)
							   .addBox(-4.0F, -32.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(16, 16)
							   .addBox(-4.0F, -24.0F, -1.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("right_arm",
				CubeListBuilder.create().texOffs(40, 16)
							   .addBox(-8.0F, -24.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("left_arm",
				CubeListBuilder.create().texOffs(32, 48)
							   .addBox(4.0F, -24.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("right_leg",
				CubeListBuilder.create().texOffs(0, 16)
							   .addBox(-4.0F, -12.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		root.addOrReplaceChild("left_leg",
				CubeListBuilder.create().texOffs(16, 48)
							   .addBox(0.0F, -12.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// Rotate head based on where the entity is looking
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);

		// Copy zombie walking animation for arms and legs
		this.rightLeg.xRot = (float) Math.cos(limbSwing * 1.0F) * 1.4F * limbSwingAmount;
		this.leftLeg.xRot = (float) Math.cos(limbSwing * 1.0F + Math.PI) * 1.4F * limbSwingAmount;

		this.rightArm.xRot = (float) Math.cos(limbSwing * 1.0F + Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.leftArm.xRot = (float) Math.cos(limbSwing * 1.0F) * 2.0F * limbSwingAmount * 0.5F;
	}
}
