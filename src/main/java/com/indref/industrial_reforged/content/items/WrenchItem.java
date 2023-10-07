package com.indref.industrial_reforged.content.items;

import com.indref.industrial_reforged.api.blocks.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

import static net.minecraft.world.level.block.Block.popResource;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 100;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos clickPos = useOnContext.getClickedPos();
        Block wrenchableBlock = level.getBlockState(clickPos).getBlock();
        ItemStack itemInHand = useOnContext.getItemInHand();
        if (!level.isClientSide) {
            if (wrenchableBlock instanceof IWrenchable) {
                if (((IWrenchable) wrenchableBlock).getDropItem() == null) {
                    popResource(level, clickPos, wrenchableBlock.asItem().getDefaultInstance());
                } else {
                    popResource(level, clickPos, new ItemStack(((IWrenchable) wrenchableBlock).getDropItem()));
                }
                level.removeBlock(clickPos, false);
                if (isDamageable(itemInHand)) {
                    itemInHand.hurtAndBreak(1, Objects.requireNonNull(useOnContext.getPlayer()), (player) -> {
                        player.broadcastBreakEvent(useOnContext.getHand());
                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
}
