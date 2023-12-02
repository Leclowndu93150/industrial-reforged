package com.indref.industrial_reforged.registries.blocks.multiblocks;

import com.indref.industrial_reforged.IndustrialReforged;
import com.indref.industrial_reforged.api.multiblocks.IMultiBlockController;
import com.indref.industrial_reforged.api.multiblocks.IMultiblock;
import com.indref.industrial_reforged.api.tiers.templates.CrucibleTier;
import com.indref.industrial_reforged.registries.blockentities.CrucibleBlockEntity;
import com.indref.industrial_reforged.registries.blockentities.FireboxBlockEntity;
import com.indref.industrial_reforged.registries.multiblocks.CrucibleMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class CrucibleControllerBlock extends BaseEntityBlock implements IMultiBlockController {
    private final CrucibleTier tier;

    public CrucibleControllerBlock(Properties properties, CrucibleTier crucibleTier) {
        super(properties);
        tier = crucibleTier;
    }

    public CrucibleTier getTier() {
        return tier;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return tier.getController().asItem().getDefaultInstance();
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
            NetworkHooks.openScreen((ServerPlayer) player, (CrucibleBlockEntity) level.getBlockEntity(blockPos), blockPos);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new CrucibleBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public IMultiblock getMultiblock() {
        return CrucibleMultiblock.CERAMIC;
    }
}
