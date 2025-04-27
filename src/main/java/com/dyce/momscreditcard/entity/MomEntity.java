package com.dyce.momscreditcard.entity;

import com.dyce.momscreditcard.sound.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class MomEntity extends PathfinderMob {
    private boolean isChasing = false;
    private boolean isPlayingMusic = false;
    private boolean isFleeing = false;
    private int fleeTimer = 0;
    private int ambientSoundTimer = 0;
    private int chaseSoundTimer = 0;

    private double baseSpeed = 0.08;
    private double fleeSpeed = 0.25;
    private double chaseSpeed = baseSpeed;

    public MomEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F); // Allows pathfinding in water
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world); // Uses ground navigation
    }



    // Set entity attributes
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                  .add(Attributes.MAX_HEALTH, 100.0)
                  .add(Attributes.MOVEMENT_SPEED, 0.1)
                  .add(Attributes.ATTACK_DAMAGE, 15.0)
                  .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new BreakDoorGoal(this, difficulty -> true)); // Always allow breaking doors

    }

    @Override
    public Component getName() {
        return Component.literal("Your Mom");
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Clear inventory
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }

            // Send system message
            player.sendSystemMessage(Component.literal("MOM HAS TAKEN ALL YOUR STUFF!"));

            // Correctly remove Mom without playing death sound
            this.discard();
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (isFleeing) {
                fleeTimer--;
                if (fleeTimer <= 0) {
                    isFleeing = false;
                    chaseSpeed += 0.1; // Ensure speed increases by 0.1 per hit
                    setSpeed(chaseSpeed); // Reset to new chase speed
                    isChasing = true;
                }
            } else {
                Player closestPlayer = this.level().getNearestPlayer(this, 40.0D);
                if (closestPlayer != null && this.hasLineOfSight(closestPlayer)) {
                    startChaseMode();
                } else {
                    stopChaseMode();
                }

                if (isChasing) {
                    if (chaseSoundTimer-- <= 0) {
                        playChaseSounds();
                        chaseSoundTimer = 40 + random.nextInt(60);
                    }
                } else {
                    if (ambientSoundTimer-- <= 0) {
                        playAmbientSounds();
                        ambientSoundTimer = 200 + random.nextInt(400);
                    }
                }
            }
        }
    }

    private void fleeFromPlayer(Player player) {
        if (!isFleeing) {
            isFleeing = true;
            fleeTimer = 60; // 3 seconds (60 ticks)
            setSpeed(fleeSpeed); // Temporarily increase speed

            // Run directly away from the player
            Vec3 fleeDirection = this.position().subtract(player.position()).normalize().scale(10);
            Vec3 fleeTarget = this.position().add(fleeDirection);
            this.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.5D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && source.getEntity() instanceof Player) {
            fleeFromPlayer((Player) source.getEntity());
        }
        return flag;
    }


    private void startChaseMode() {
        if (!isChasing) {
            isChasing = true;
            startChaseMusic();
            setSpeed(chaseSpeed);
        }
    }

    private void stopChaseMode() {
        if (isChasing) {
            isChasing = false;
            stopChaseMusic();
            setSpeed(baseSpeed);
        }
    }


    private void startChaseMusic() {
        if (!isPlayingMusic) {
            this.level().playSound(null, this.blockPosition(), SoundRegistry.MOM_CHASE_MUSIC.get(), SoundSource.MUSIC, 1.0F, 1.0F);
            isPlayingMusic = true;
        }
    }

    private void stopChaseMusic() {
        if (isPlayingMusic) {
            isPlayingMusic = false;
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.getPlayers(player -> player.distanceTo(this) < 50).forEach(player -> {
                    player.connection.send(new ClientboundStopSoundPacket(SoundRegistry.MOM_CHASE_MUSIC.get().getLocation(), SoundSource.MUSIC));
                });
            }
        }
    }

    private void playChaseSounds() {
        switch (random.nextInt(5)) {
            case 0 -> this.playSound(SoundRegistry.MOM_CHASE1.get(), 1.0F, 1.0F);
            case 1 -> this.playSound(SoundRegistry.MOM_CHASE2.get(), 1.0F, 1.0F);
            case 2 -> this.playSound(SoundRegistry.MOM_CHASE3.get(), 1.0F, 1.0F);
            case 3 -> this.playSound(SoundRegistry.MOM_CHASE4.get(), 1.0F, 1.0F);
            case 4 -> this.playSound(SoundRegistry.MOM_CHASE5.get(), 1.0F, 1.0F);
        }
    }

    private void playAmbientSounds() {
        switch (random.nextInt(3)) {
            case 0 -> this.playSound(SoundRegistry.MOM_AMBIENT1.get(), 1.0F, 1.0F);
            case 1 -> this.playSound(SoundRegistry.MOM_AMBIENT2.get(), 1.0F, 1.0F);
            default -> this.playSound(SoundRegistry.MOM_AMBIENT3.get(), 1.0F, 1.0F);
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        spawnDad(); // Try to spawn Dad
        return SoundRegistry.MOM_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos entityPos = this.blockPosition();
        int radius = 5; // Check in a 5-block radius

        // Get current light level at entity's position
        int lightLevel = this.level().getMaxLocalRawBrightness(entityPos);

        // **Break Torches and Light Sources Based on Light Level**
        if (lightLevel >= 8) {
            for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-radius, -1, -radius), entityPos.offset(radius, 1, radius))) {
                BlockState blockState = this.level().getBlockState(pos);

                // **Destroy torches, wall torches, and lanterns**
                if (blockState.is(Blocks.TORCH) || blockState.is(Blocks.WALL_TORCH) || blockState.is(Blocks.LANTERN)) {
                    this.level().destroyBlock(pos, false); // Break without dropping the item
                    this.level().playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);

                    // **Apply blindness effect to the player for extra horror**
                    if (this.getTarget() instanceof Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 70, 0, false, false));
                    }
                }
            }
        }

        // **Break Doors Instantly**
        for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-1, 0, -1), entityPos.offset(1, 1, 1))) {
            BlockState blockState = this.level().getBlockState(pos);

            if (blockState.getBlock() instanceof DoorBlock) {
                this.level().destroyBlock(pos, false); // Instantly break the door
                this.level().playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
            }
        }

        // **Ensure Mom Always Chases the Player**
        Player closestPlayer = this.level().getNearestPlayer(this, 100.0D);
        if (closestPlayer != null) {
            startChaseMode(); // Call without arguments
        }
    }


    @Override
    public boolean hasLineOfSight(Entity entity) {
        return true; // Mom can see through walls
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    @Override
    public float getWaterSlowDown() {
        return 0.8F; // Reduces water movement slowdown
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isInWater() || this.isInLava()) {
            this.moveRelative(0.02F, travelVector); // Smooth movement in water/lava
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D)); // Slight slow down in liquids

            // Apply upward movement to prevent sinking
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
        } else {
            super.travel(travelVector); // Use default movement on land
        }
    }




    // **50% chance to spawn Dad**
    private void spawnDad() {
        if (!this.level().isClientSide && this.random.nextFloat() < 0.5F) {
            DadEntity dad = new DadEntity(ModEntities.DAD.get(), this.level());
            dad.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            this.level().addFreshEntity(dad);
        }
    }

    private void setSpeed(double speed) {
        AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.setBaseValue(speed);
        }
    }
}