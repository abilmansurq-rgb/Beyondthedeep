package com.beyondthedeep;

import com.beyondthedeep.blocks.ModBlocks;
import com.beyondthedeep.items.MobCardItem;
import com.beyondthedeep.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeyondTheDeep implements ModInitializer {
	public static final String MOD_ID = "beyondthedeep";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ItemGroup BEYOND_THE_DEEP_TAB = Registry.register(
			Registries.ITEM_GROUP,
			new Identifier(MOD_ID, "beyond_the_deep_tab"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemGroup.beyondthedeep.beyond_the_deep_tab"))
					.icon(() -> new ItemStack(ModBlocks.VOID_ORE.asItem()))
					.entries((context, entries) -> {
						entries.add(ModBlocks.VOID_ORE.asItem());
						entries.add(ModItems.VOID_SHARD.asItem());
						entries.add(ModItems.RAW_VOID_ALLOY.asItem());
						entries.add(ModItems.VOID_ALLOY.asItem());
						entries.add(ModItems.MOB_CARD.asItem());
						entries.add(ModItems.SOUL_EXTRACTOR.asItem());
						entries.add(ModItems.VOID_HELMET.asItem());
						entries.add(ModItems.VOID_CHESTPLATE.asItem());
						entries.add(ModItems.VOID_LEGGINGS.asItem());
						entries.add(ModItems.VOID_BOOTS.asItem());
					})
					.build()
	);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();

		// Регистрация события смерти
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (source.getAttacker() instanceof PlayerEntity player) {
				ItemStack heldItem = player.getMainHandStack();

				if (heldItem.getItem() == ModItems.SOUL_EXTRACTOR) {
					for (ItemStack stack : player.getInventory().main) {
						// Безопасная проверка NBT
						if (stack.getItem() == ModItems.MOB_CARD && (stack.getNbt() == null || !stack.getNbt().contains("MobName"))) {

							MobCardItem.setMob(stack, entity.getType().getName().getString());

							if (entity.getWorld() instanceof ServerWorld serverWorld) {
								serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE,
										entity.getX(), entity.getY() + 0.5, entity.getZ(),
										15, 0.2, 0.2, 0.2, 0.05);
								entity.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 0.5F, 1.0F);
							}
							break;
						}
					}
				}
			}
		});

		LOGGER.info("Beyond The Deep initialized!");
	}
}