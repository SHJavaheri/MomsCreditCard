package com.dyce.momscreditcard.client.renderer;

import com.dyce.momscreditcard.MomsCreditCardMod;
import com.dyce.momscreditcard.client.model.MomEntityModel;
import com.dyce.momscreditcard.entity.MomEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MomRenderer extends MobRenderer<MomEntity, MomEntityModel<MomEntity>> {
    private static final ResourceLocation MOM_TEXTURE = new ResourceLocation(MomsCreditCardMod.MOD_ID, "textures/entity/mom.png");

    public MomRenderer(EntityRendererProvider.Context context) {
        super(context, new MomEntityModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MomEntity entity) {
        return MOM_TEXTURE;
    }
}
