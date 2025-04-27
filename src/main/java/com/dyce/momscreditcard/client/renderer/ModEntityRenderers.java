package com.dyce.momscreditcard.client.renderer;

import com.dyce.momscreditcard.entity.ModEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MOM.get(), MomRenderer::new);
        event.registerEntityRenderer(ModEntities.DAD.get(), DadRenderer::new);
    }
}
