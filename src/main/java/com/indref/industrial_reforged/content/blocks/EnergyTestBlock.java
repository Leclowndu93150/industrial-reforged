package com.indref.industrial_reforged.content.blocks;

import com.indref.industrial_reforged.api.blocks.IWrenchable;
import com.indref.industrial_reforged.api.capabilities.IRCapabilities;
import com.indref.industrial_reforged.api.capabilities.energy.EnergyStorageProvider;
import com.indref.industrial_reforged.api.energy.blocks.IEnergyBlock;
import com.indref.industrial_reforged.content.blockentities.impl.EnergyTestBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyTestBlock extends BaseEntityBlock implements IWrenchable, IEnergyBlock {
    public EnergyTestBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EnergyTestBE(blockPos, blockState);
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(blockPos);
            if (entity instanceof IEnergyBlock) {
                entity.getCapability(IRCapabilities.ENERGY).ifPresent((energyStorage) -> energyStorage.setEnergyStored(1008));
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public EnergyStorageProvider getEnergyStorage() {
        return new EnergyStorageProvider();
    }
}
