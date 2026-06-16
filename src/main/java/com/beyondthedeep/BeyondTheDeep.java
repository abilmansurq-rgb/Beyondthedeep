package com.beyondthedeep;

import com.beyondthedeep.blocks.ModBlocks;
import com.beyondthedeep.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.block.Block;

// В методе onInitialize добавь:
// Это скажет игре, что void_ore — это руда, которую можно копать киркой
// и для нее нужна железная кирка.

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeyondTheDeep implements ModInitializer {
	public static final String MOD_ID = "beyondthedeep";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ItemGroup BEYOND_THE_DEEP_TAB = Registry.register(
			Registries.ITEM_GROUP,
			new Identifier("beyondthedeep", "beyond_the_deep_tab"),
			FabricItemGroup.builder()
					.displayName(net.minecraft.text.Text.translatable("itemGroup.beyondthedeep.beyond_the_deep_tab"))
					.icon(() -> new ItemStack(com.beyondthedeep.blocks.ModBlocks.VOID_ORE.asItem()))
					.entries((context, entries) -> {
						entries.add(com.beyondthedeep.blocks.ModBlocks.VOID_ORE.asItem());
						entries.add(ModItems.VOID_SHARD.asItem());
					})
					.build()
	);
	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		LOGGER.info("Hello Fabric world!");
	}
}