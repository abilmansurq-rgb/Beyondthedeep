package com.beyondthedeep.items;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.EnumMap;
import java.util.function.Supplier;

public class VoidArmorMaterial implements ArmorMaterial {
    private static final EnumMap<ArmorItem.Type, Integer> PROTECTION = new EnumMap<>(ArmorItem.Type.class);
    static {
        PROTECTION.put(ArmorItem.Type.BOOTS, 2);
        PROTECTION.put(ArmorItem.Type.LEGGINGS, 5);
        PROTECTION.put(ArmorItem.Type.CHESTPLATE, 6);
        PROTECTION.put(ArmorItem.Type.HELMET, 2);
    }

    @Override
    public int getDurability(ArmorItem.Type type) { return 25; }
    @Override
    public int getProtection(ArmorItem.Type type) { return PROTECTION.get(type); }
    @Override
    public int getEnchantability() { return 22; }
    @Override
    public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE; }
    @Override
    public Ingredient getRepairIngredient() { return Ingredient.ofItems(ModItems.VOID_ALLOY); }
    @Override
    public String getName() { return "void_alloy"; }
    @Override
    public float getToughness() { return 2.5F; }
    @Override
    public float getKnockbackResistance() { return 0.1F; }
}