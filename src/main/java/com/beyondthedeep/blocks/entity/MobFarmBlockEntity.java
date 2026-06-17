package com.beyondthedeep.blocks.entity;

import com.beyondthedeep.blocks.ModBlockEntities; // Это мы создадим чуть позже
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class MobFarmBlockEntity extends BlockEntity {

    // Переменные для логики
    private int progress = 0;
    private int fuelTime = 0;
    private int maxProgress = 100; // Сколько тиков на один "улов"

    public MobFarmBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_FARM_BLOCK_ENTITY, pos, state);
    }

    // Здесь мы будем сохранять прогресс при выходе из игры
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("mob_farm.progress", progress);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getInt("mob_farm.progress");
    }
}