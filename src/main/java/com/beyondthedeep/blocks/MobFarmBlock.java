package com.beyondthedeep.blocks;

import com.beyondthedeep.blocks.entity.MobFarmBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MobFarmBlock extends BlockWithEntity {
    public MobFarmBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MobFarmBlockEntity(pos, state);
    }
}