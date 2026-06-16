package com.beyondthedeep.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Создаем предмет
    public static final Item VOID_SHARD = registerItem("void_shard",
            new Item(new FabricItemSettings()));

    // Вспомогательный метод для регистрации
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("beyondthedeep", name), item);
    }

    public static void registerModItems() {
        System.out.println("Registering ModItems for " + "beyondthedeep");
    }
}