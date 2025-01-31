package com.indref.industrial_reforged.content.multiblocks;

import com.indref.industrial_reforged.api.tiers.FireboxTier;
import com.portingdeadmods.portingdeadlibs.api.multiblocks.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IFireboxMultiblock extends Multiblock {
    FireboxTier getTier();

    /**
     * @param multiblockPos the blockpos of a block that is part of the multi
     * @return The controller block controllerPos of the multiblock
     */
    @Nullable BlockPos getControllerPos(BlockPos multiblockPos, Level level);
}
