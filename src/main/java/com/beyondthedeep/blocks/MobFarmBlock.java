package com.beyondthedeep.blocks;

import com.beyondthedeep.blocks.entity.MobFarmBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MobFarmBlock extends BlockWithEntity {
    public MobFarmBlock(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            System.out.println("Клик по блоку!");
            // Получаем BlockEntity и открываем GUI
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MobFarmBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends net.minecraft.block.entity.BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
// Замени свою строку 48 на эту:
        return checkType(type, ModBlockEntities.MOB_FARM_BLOCK_ENTITY, MobFarmBlockEntity::tick);
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MobFarmBlockEntity) {
                MobFarmBlockEntity inv = (MobFarmBlockEntity) blockEntity;
                for (int i = 0; i < inv.size(); i++) {
                    net.minecraft.item.ItemStack stack = inv.getStack(i);
                    if (!stack.isEmpty()) {
                        net.minecraft.block.Block.dropStack(world, pos, stack);
                    }
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

}