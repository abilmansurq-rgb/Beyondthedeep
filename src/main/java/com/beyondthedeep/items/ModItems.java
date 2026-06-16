package com.beyondthedeep.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Создаем предмет
    public static final Item VOID_SHARD = registerItem("void_shard",
            new Item(new FabricItemSettings()));
    public static final Item RAW_VOID_ALLOY = registerItem("raw_void_alloy",
            new Item(new FabricItemSettings()));
    public static final Item VOID_ALLOY = registerItem("void_alloy",
            new Item(new FabricItemSettings()));
    // Регистрация брони
    public static final ArmorItem VOID_HELMET = registerItem("void_helmet",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final ArmorItem VOID_CHESTPLATE = registerItem("void_chestplate",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final ArmorItem VOID_LEGGINGS = registerItem("void_leggings",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final ArmorItem VOID_BOOTS = registerItem("void_boots",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    // Вспомогательный метод для регистрации
    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier("beyondthedeep", name), item);
    }

    public static void registerModItems() {
        System.out.println("Registering ModItems for " + "beyondthedeep");
    }
}