package com.beyondthedeep.items.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class VoidRequiemItem extends SwordItem {
    private static final UUID DAMAGE_BONUS_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public VoidRequiemItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        // 1. Получаем базовые модификаторы
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = super.getAttributeModifiers(stack, slot);

        if (slot == EquipmentSlot.MAINHAND) {
            float bonus = stack.getOrCreateNbt().getFloat("DamageBonus");

            // Если бонуса нет, возвращаем стандарт
            if (bonus <= 0) return modifiers;

            // 2. Создаем НОВУЮ карту
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

            // 3. Перебираем старые модификаторы
            modifiers.forEach((attribute, modifier) -> {
                if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
                    // ЕСЛИ ЭТО УРОН — МЕНЯЕМ ЕГО
                    builder.put(attribute, new EntityAttributeModifier(
                            modifier.getId(),
                            modifier.getName(),
                            modifier.getValue() + bonus, // Добавляем бонус к существующему значению
                            modifier.getOperation()
                    ));
                } else {
                    // ЕСЛИ ЭТО СКОРОСТЬ ИЛИ ЧТО-ТО ЕЩЕ — ОСТАВЛЯЕМ КАК ЕСТЬ
                    builder.put(attribute, modifier);
                }
            });

            return builder.build();
        }
        return modifiers;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        float bonus = stack.getOrCreateNbt().getFloat("DamageBonus");
        tooltip.add(Text.translatable("tooltip.beyondthedeep.damage_bonus")
                .append(Text.literal(": " + String.format("%.2f", bonus)))
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
        super.appendTooltip(stack, world, tooltip, context);
    }
}