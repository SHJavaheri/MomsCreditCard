package com.dyce.momscreditcard;

import com.dyce.momscreditcard.registry.ModItems;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.CreativeModeTabs;

@Mod.EventBusSubscriber(modid = MomsCreditCardMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            System.out.println("✅ DEBUG: Item added to Tools & Utilities tab!");
            event.accept(ModItems.MOMS_CREDIT_CARD.get());
        }
    }

}
