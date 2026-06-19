package com.beyondthedeep.event;

import com.beyondthedeep.items.ModItems; // Твой класс с предметами
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableHandler {
    // ID таблицы лута Вардена
    private static final Identifier WARDEN_ID = new Identifier("minecraft", "entities/warden");

    public static void registerLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Проверяем, что это таблица именно Вардена
            if (WARDEN_ID.equals(id)) {
                // Создаем пул (группу) дропа
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1)) // 1 раз
                        .with(ItemEntry.builder(ModItems.VOID_CORE)
                        .conditionally(RandomChanceLootCondition.builder(0.66f))); // Наш предмет с шансом падает.

                // Добавляем пул в таблицу
                tableBuilder.pool(poolBuilder);
            }
        });
    }
}