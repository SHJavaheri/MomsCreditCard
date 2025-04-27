package com.dyce.momscreditcard.client.renderer;

import com.dyce.momscreditcard.client.model.DadEntityModel;
import com.dyce.momscreditcard.entity.DadEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DadRenderer extends MobRenderer<DadEntity, DadEntityModel<DadEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("momscreditcard", "textures/entity/dad.png");

    public DadRenderer(EntityRendererProvider.Context context) {
        super(context, new DadEntityModel<>(context.bakeLayer(DadEntityModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DadEntity entity) {
        return TEXTURE;
    }
}
