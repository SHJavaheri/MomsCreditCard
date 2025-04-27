package com.dyce.momscreditcard.registry;

import com.dyce.momscreditcard.MomsCreditCardMod;
import com.dyce.momscreditcard.items.MomsCreditCardItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MomsCreditCardMod.MOD_ID);

    public static final RegistryObject<Item> MOMS_CREDIT_CARD = ITEMS.register("moms_credit_card",
            () -> new MomsCreditCardItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
