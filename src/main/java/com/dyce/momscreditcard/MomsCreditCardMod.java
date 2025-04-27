package com.dyce.momscreditcard;

import com.dyce.momscreditcard.client.ClientSetup;
import com.dyce.momscreditcard.entity.ModEntities;
import com.dyce.momscreditcard.registry.ModItems;
import com.dyce.momscreditcard.sound.SoundRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MomsCreditCardMod.MOD_ID)
public class MomsCreditCardMod {
    public static final String MOD_ID = "momscreditcard";

    public MomsCreditCardMod() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        // Register items
        ModItems.register(modEventBus);

        // Register Entities
        ModEntities.register();
        MinecraftForge.EVENT_BUS.register(ClientSetup.class);

        // Register Sounds
        SoundRegistry.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        System.out.println("HELLO FROM COMMON SETUP");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        System.out.println("HELLO FROM CLIENT SETUP");
    }
}
