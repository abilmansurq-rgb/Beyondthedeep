package com.beyondthedeep.items;

import net.minecraft.client.item.TooltipContext; // Важный импорт!
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class MobCardItem extends Item {
    public MobCardItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Проверка NBT
        if (stack.getNbt() != null && stack.getNbt().contains("MobName")) {
            String mobName = stack.getNbt().getString("MobName");
            // Вместо:
// tooltip.add(Text.literal("Сущность: " + mobName));

// Используй:
            tooltip.add(Text.translatable("tooltip.beyondthedeep.mob_name", mobName));
        } else {
            tooltip.add(Text.translatable("tooltip.beyondthedeep.empty_card"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
    public static void setMob(ItemStack stack, String mobName) {
        // Получаем или создаем NBT-тег
        stack.getOrCreateNbt().putString("MobName", mobName);
    }
}