package com.beyondthedeep.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class VoidItem extends Item {
    public VoidItem(Settings settings) {
        super(settings);
    }

    // Здесь можно переопределить методы для всех предметов из "пустоты"
    // Например, они все могут светиться в инвентаре:
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}