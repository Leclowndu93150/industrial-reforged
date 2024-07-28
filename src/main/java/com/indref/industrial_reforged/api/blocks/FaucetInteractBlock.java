package com.indref.industrial_reforged.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

/**
 * Implement this interface on your block if you
 * want the molten metal of your faucet to go down further
 * than the max y
 */
public interface FaucetInteractBlock {
    /**
     * @return the y pos the molten metal should reach down to
     */
    float getShapeMaxY(BlockGetter blockGetter, BlockPos blockPos);
}
