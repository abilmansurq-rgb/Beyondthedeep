package com.beyondthedeep.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Создаем саму Пустотную Руду (копируем прочность алмазной руды)
    public static final Block VOID_ORE = registerBlock("void_ore",
            new Block(FabricBlockSettings.copy(Blocks.DIAMOND_ORE)
                    .strength(4.5f, 6.0f)
                    .requiresTool()));

    // Вспомогательный метод для регистрации блока и его предмета в инвентаре
    private static Block registerBlock(String name, Block block) {
        // Использован правильный MOD_ID твоего нового мода: "beyondthedeep"
        Identifier id = new Identifier("beyondthedeep", name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    // Главный метод для инициализации всех блоков мода
    public static void registerModBlocks() {
        // Добавь эту строчку, чтобы игра при запуске узнала про твой блок:
        System.out.println("Registering ModBlocks for " + "beyondthedeep");
    }}