package com.dyce.momscreditcard.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.dyce.momscreditcard.MomsCreditCardMod; // Change this to your mod's main class!

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MomsCreditCardMod.MOD_ID);

    //Mom Sounds
    public static final RegistryObject<SoundEvent> MOM_AMBIENT1 = register("mom_ambient1");
    public static final RegistryObject<SoundEvent> MOM_AMBIENT2 = register("mom_ambient2");
    public static final RegistryObject<SoundEvent> MOM_AMBIENT3 = register("mom_ambient3");

    public static final RegistryObject<SoundEvent> MOM_CHASE1 = register("mom_chase1");
    public static final RegistryObject<SoundEvent> MOM_CHASE2 = register("mom_chase2");
    public static final RegistryObject<SoundEvent> MOM_CHASE3 = register("mom_chase3");
    public static final RegistryObject<SoundEvent> MOM_CHASE4 = register("mom_chase4");
    public static final RegistryObject<SoundEvent> MOM_CHASE5 = register("mom_chase5");

    public static final RegistryObject<SoundEvent> MOM_DEATH = register("mom_death");
    public static final RegistryObject<SoundEvent> MOM_CHASE_MUSIC = register("mom_chase_music");
    public static final RegistryObject<SoundEvent> MOM_CHASE_MUSIC_STOP = register("mom_chase_music_stop");


    //Dad Sounds
    public static final RegistryObject<SoundEvent> DAD_BOSS_MUSIC = register("dad_boss_music");
    public static final RegistryObject<SoundEvent> DAD_DEATH = register("dad_death");


    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MomsCreditCardMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
