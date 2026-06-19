package com.beyondthedeep.items;

import com.beyondthedeep.items.custom.VoidRequiemItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
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
    public static final Item MOB_CARD = registerItem("mob_card",
            new MobCardItem(new FabricItemSettings().maxCount(1)));
    // Регистрация брони
    public static final ArmorItem VOID_HELMET = registerItem("void_helmet",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final ArmorItem VOID_CHESTPLATE = registerItem("void_chestplate",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final ArmorItem VOID_LEGGINGS = registerItem("void_leggings",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final ArmorItem VOID_BOOTS = registerItem("void_boots",
            new ArmorItem(ModArmorMaterials.VOID_ALLOY, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    // Регистрация предметов с функционалом
    // Регистрация Вытягивателя Душ
// Параметры: (название_в_игре, объект_предмета)
    // Добавляем Ядро Бездны
    public static final Item VOID_CORE = registerItem("void_core", new Item(new FabricItemSettings().maxCount(1).fireproof()));

    // Добавляем регистрацию меча в общий поток
    public static final SwordItem VOID_ALLOY_SWORD = registerItem("void_alloy_sword",
            new SwordItem(ModToolMaterials.VOID_MATERIAL, 4, -2.4f, new FabricItemSettings()));
    public static final Item SOUL_EXTRACTOR = registerItem("soul_extractor",
            new VoidTool(
                    ModToolMaterials.VOID_MATERIAL, // МАТЕРИАЛ: можно заменить на свой кастомный (например, VOID_MATERIAL)
                    1,                  // УРОН: чем меньше, тем дольше моб будет жить (идеально для "вытягивания")
                    -2.4F,              // СКОРОСТЬ АТАКИ: стандарт для мечей
                    new FabricItemSettings().maxCount(1) // НАСТРОЙКИ: maxCount(1) нужен, так как это уникальный инструмент
            )
    );
    public static final Item VOID_REQUIEM = registerItem("void_requiem",
            new VoidRequiemItem(
                    ModToolMaterials.VOID_MATERIAL,
                    4,
                    -2.5f,
                    new FabricItemSettings().maxCount(1)));


    // Вспомогательный метод для регистрации
    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier("beyondthedeep", name), item);
    }

    public static void registerModItems() {
        System.out.println("Registering ModItems for " + "beyondthedeep");
    }
}