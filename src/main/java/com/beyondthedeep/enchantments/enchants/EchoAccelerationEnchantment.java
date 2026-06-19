package com.beyondthedeep.enchantments.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class EchoAccelerationEnchantment extends Enchantment {
    public EchoAccelerationEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMaxLevel() { return 3; }
    @Override
    public boolean isTreasure() {
        return true; // Сделаем их "сокровищными", чтобы они не выпадали просто так
    }
}
