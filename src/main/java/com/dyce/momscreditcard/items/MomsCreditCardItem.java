package com.dyce.momscreditcard.items;

import com.dyce.momscreditcard.entity.ModEntities;
import com.dyce.momscreditcard.entity.MomEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class MomsCreditCardItem extends Item {
    public MomsCreditCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            Random random = new Random();

            // Give multiple items (2 to 5)
            int numberOfItems = 2 + random.nextInt(4);
            for (int i = 0; i < numberOfItems; i++) {
                ItemStack randomItem = getRandomItem();
                player.addItem(randomItem);
            }

            player.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);

            // 10% chance to spawn "Mom"
            if (random.nextInt(100) < 10) {
                MomEntity mom = ModEntities.MOM.get().create(level);
                if (mom != null) {
                    // Get player's direction (ignores vertical rotation)
                    Vec3 lookDirection = player.getLookAngle();
                    Vec3 horizontalDirection = new Vec3(lookDirection.x, 0, lookDirection.z).normalize().scale(3); // Move 3 blocks ahead

                    // Find intended spawn position
                    BlockPos spawnPos = new BlockPos((int) (player.getX() + horizontalDirection.x), (int) player.getY(), (int) (player.getZ() + horizontalDirection.z));

                    // Adjust for solid ground (Avoids spawning in the air)
                    while (!level.getBlockState(spawnPos).isSolidRender(level, spawnPos) && spawnPos.getY() > level.getMinBuildHeight()) {
                        spawnPos = spawnPos.below();
                    }

                    // Ensure it's not inside a block (Move up if needed)
                    while (level.getBlockState(spawnPos).isSolidRender(level, spawnPos) && spawnPos.getY() < level.getMaxBuildHeight()) {
                        spawnPos = spawnPos.above();
                    }

                    // Set Mom's position
                    mom.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                    level.addFreshEntity(mom);

                    System.out.println("⚠️ MOM HAS ARRIVED IN FRONT OF YOU!");
                } else {
                    System.out.println("❌ ERROR: Mom entity failed to spawn.");
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }







    private ItemStack getRandomItem() {
        Random random = new Random();

        // Define different item rarity groups
        Item[] commonItems = new Item[]{
                Items.APPLE, Items.BREAD, Items.STICK, Items.COBBLESTONE, Items.COAL, Items.LEATHER_BOOTS
        };

        Item[] rareItems = new Item[]{
                Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT, Items.EXPERIENCE_BOTTLE, Items.ENDER_PEARL,
                Items.TNT, Items.EMERALD, Items.NETHERITE_SCRAP, Items.GOLDEN_APPLE
        };

        Item[] legendaryItems = new Item[]{
                Items.NETHERITE_INGOT, Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING, Items.ELYTRA,
                Items.BEACON, Items.NETHER_STAR, Items.TRIDENT
        };

        // Random chance system
        int roll = random.nextInt(100); // Generate a number between 0-99

        if (roll < 60) { // 60% chance for common items
            return new ItemStack(commonItems[random.nextInt(commonItems.length)], 1 + random.nextInt(3));
        } else if (roll < 90) { // 30% chance for rare items
            return new ItemStack(rareItems[random.nextInt(rareItems.length)], 1 + random.nextInt(2));
        } else { // 10% chance for legendary items
            return new ItemStack(legendaryItems[random.nextInt(legendaryItems.length)], 1);
        }
    }
}
