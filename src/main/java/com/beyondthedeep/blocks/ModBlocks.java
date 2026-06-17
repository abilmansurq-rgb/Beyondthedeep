package com.beyondthedeep.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import static net.minecraft.block.Blocks.STONE;

public class ModBlocks {
    public static final Block MOB_FARM_BLOCK = registerBlock("mob_farm_block",
            new MobFarmBlock(FabricBlockSettings.create().strength(4.0f)));
    // Создаем саму Пустотную Руду (копируем прочность алмазной руды)
    public static final Block VOID_ORE = registerBlock("void_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(STONE).strength(2f), UniformIntProvider.create(3, 9)));
    private static Block registerBlock(String name, Block block) {
        Identifier id = new Identifier("beyondthedeep", name);
        // Сначала регистрируем блок
        Block registeredBlock = Registry.register(Registries.BLOCK, id, block);
        // Потом предмет
        Registry.register(Registries.ITEM, id, new BlockItem(registeredBlock, new FabricItemSettings()));
        return registeredBlock;
    }
    // Главный метод для инициализации всех блоков мода
    public static void registerModBlocks() {
        // Добавь эту строчку, чтобы игра при запуске узнала про твой блок:
        System.out.println("Registering ModBlocks for " + "beyondthedeep");
    }}