package com.dyce.momscreditcard.entity;

import com.dyce.momscreditcard.MomsCreditCardMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = MomsCreditCardMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MomsCreditCardMod.MOD_ID);

    public static final RegistryObject<EntityType<MomEntity>> MOM = ENTITIES.register("mom",
            () -> EntityType.Builder.of(MomEntity::new, MobCategory.MONSTER)
                                    .sized(0.6F, 1.95F) // **Mom is player-sized**
                                    .build(new ResourceLocation(MomsCreditCardMod.MOD_ID, "mom").toString()));

    public static final RegistryObject<EntityType<DadEntity>> DAD = ENTITIES.register("dad",
            () -> EntityType.Builder.of(DadEntity::new, MobCategory.MONSTER)
                                    .sized(0.9F, 2.2F) // **Dad is slightly taller**
                                    .build(new ResourceLocation(MomsCreditCardMod.MOD_ID, "dad").toString()));

    // **Register entity attributes**
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(MOM.get(), MomEntity.createAttributes().build());
        event.put(DAD.get(), DadEntity.createAttributes().build());
    }

    // **Ensure ENTITIES is registered in the event bus**
    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
