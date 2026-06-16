package com.beyondthedeep.items;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class ModArmorMaterials {
    public static final ArmorMaterial VOID_ALLOY = new VoidArmorMaterial();

    public static boolean hasFullVoidArmor(PlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

        return !helmet.isEmpty() && helmet.isOf(ModItems.VOID_HELMET) &&
                !chest.isEmpty() && chest.isOf(ModItems.VOID_CHESTPLATE) &&
                !legs.isEmpty() && legs.isOf(ModItems.VOID_LEGGINGS) &&
                !boots.isEmpty() && boots.isOf(ModItems.VOID_BOOTS);
    }
}