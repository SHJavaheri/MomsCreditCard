package com.dyce.momscreditcard.client;

import com.dyce.momscreditcard.MomsCreditCardMod;
import com.dyce.momscreditcard.client.model.DadEntityModel;
import com.dyce.momscreditcard.client.renderer.DadRenderer;
import com.dyce.momscreditcard.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MomsCreditCardMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DAD.get(), DadRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DadEntityModel.LAYER_LOCATION, DadEntityModel::createBodyLayer);
    }
}
