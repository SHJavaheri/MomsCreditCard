package com.dyce.momscreditcard.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvent;


public class DadEntity extends PathfinderMob {
    private int explosionCountdown = -1; // -1 means inactive
    private static final int EXPLOSION_FUSE = 10; // **10 ticks = 0.5 seconds**
    private static final float EXPLOSION_POWER = 6.0F; // **Explosion power (Stronger than TNT)**

    public DadEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world);
    }

    // **Set Dad's attributes (big, fast, deadly)**
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                  .add(Attributes.MAX_HEALTH, 150.0) // **High health**
                  .add(Attributes.MOVEMENT_SPEED, 0.35) // **Fast movement**
                  .add(Attributes.ATTACK_DAMAGE, 50.0) // **Melee damage**
                  .add(Attributes.FOLLOW_RANGE, 50.0); // **Detects players far away**
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true)); // **Aggressive melee**
        this.goalSelector.addGoal(2, new BreakDoorGoal(this, difficulty -> true)); // **Breaks doors**
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public Component getName() {
        return Component.literal("Your Dad");
    }

    @Override
    public void aiStep() {
        super.aiStep();

        Player closestPlayer = this.level().getNearestPlayer(this, 100.0D);
        if (closestPlayer != null) {
            startChaseMode();
        }

        BlockPos entityPos = this.blockPosition();

        // **Break Torches and Lanterns**
        int lightLevel = this.level().getMaxLocalRawBrightness(entityPos);
        if (lightLevel >= 8) {
            int radius = 5;
            for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-radius, -1, -radius), entityPos.offset(radius, 1, radius))) {
                BlockState blockState = this.level().getBlockState(pos);

                if (blockState.is(Blocks.TORCH) || blockState.is(Blocks.WALL_TORCH) || blockState.is(Blocks.LANTERN)) {
                    this.level().destroyBlock(pos, false); // **Break torches**
                    this.level().playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);

                    // **Blind player for horror effect**
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
                this.level().destroyBlock(pos, false); // **Instantly break the door**
                this.level().playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
            }
        }

        // **Explosion Countdown When Close to Player**
        if (!this.level().isClientSide) {
            Player explosionTarget = this.level().getNearestPlayer(this, 1.0D);
            if (explosionTarget != null) {
                startExplosionCountdown();
            }

            if (explosionCountdown >= 0) {
                explosionCountdown--;

                if (explosionCountdown == 0) {
                    explode();
                }
            }
        }
    }

    private void startChaseMode() {
        setSpeed(0.40); // **Dad gets faster when chasing**
    }

    private void startExplosionCountdown() {
        if (explosionCountdown == -1) {
            explosionCountdown = EXPLOSION_FUSE;
        }
    }

    private void explode() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), EXPLOSION_POWER, Level.ExplosionInteraction.BLOCK);
            this.discard(); // **Remove Dad after explosion**
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // **Clear player’s inventory**
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }

            // **Send system message**
            player.sendSystemMessage(Component.literal("DAD HAS OBLITERATED YOU!"));

            // **Trigger explosion immediately**
            explode();
        }
        return super.doHurtTarget(entity);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null; // **No explosion sound when Dad dies**
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // **Override HasLineOfSight to Always Return True (Dad sees through walls)**
    @Override
    public boolean hasLineOfSight(Entity entity) {
        return true;
    }

    private void setSpeed(double speed) {
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
    }
}
