package com.beyondthedeep.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ImplementedInventory extends Inventory {
    DefaultedList<ItemStack> getItems();

    @Override
    default int size() { return getItems().size(); }
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) { if (!getStack(i).isEmpty()) return false; }
        return true;
    }
    @Override
    default ItemStack getStack(int slot) { return getItems().get(slot); }
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = getStack(slot).split(count);
        if (!getStack(slot).isEmpty()) markDirty();
        return result;
    }

    @Override
    default ItemStack removeStack(int slot) {
        ItemStack result = getStack(slot);
        setStack(slot, ItemStack.EMPTY);
        return result;
    }
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
        markDirty();
    }
    @Override
    default void clear() { getItems().clear(); }
    @Override
    default void markDirty() {}
    @Override
    default boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) { return true; }
}