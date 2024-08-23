package com.indref.industrial_reforged.registries.blockentities.multiblocks.controller;

import com.indref.industrial_reforged.api.blockentities.container.ContainerBlockEntity;
import com.indref.industrial_reforged.api.capabilities.IOActions;
import com.indref.industrial_reforged.api.capabilities.IRCapabilities;
import com.indref.industrial_reforged.api.capabilities.heat.IHeatStorage;
import com.indref.industrial_reforged.api.tiers.FireboxTier;
import com.indref.industrial_reforged.registries.IRBlockEntityTypes;
import com.indref.industrial_reforged.registries.blocks.multiblocks.parts.FireboxPartBlock;
import com.indref.industrial_reforged.registries.gui.menus.FireBoxMenu;
import com.indref.industrial_reforged.tiers.FireboxTiers;
import com.indref.industrial_reforged.util.BlockUtils;
import com.indref.industrial_reforged.util.capabilities.CapabilityUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FireboxBlockEntity extends ContainerBlockEntity implements MenuProvider {
    private static final int INPUT_SLOT = 0;

    private int burnTime;
    private int maxBurnTime;
    private final FireboxTier fireboxTier;

    private BlockCapabilityCache<IHeatStorage, Direction> aboveBlockCapCache;

    public FireboxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, FireboxTier fireboxTier, int heatCapacity) {
        super(blockEntityType, blockPos, blockState);
        addItemHandler(1, (slot, itemStack) -> itemStack.getBurnTime(RecipeType.SMELTING) > 0);
        addHeatStorage(heatCapacity);
        this.fireboxTier = fireboxTier;
    }

    public FireboxBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(IRBlockEntityTypes.FIREBOX.get(), blockPos, blockState, FireboxTiers.REFRACTORY, 4000);
    }

    @Override
    public void onLoad() {
        if (level instanceof ServerLevel serverLevel) {
            this.aboveBlockCapCache = BlockCapabilityCache.create(IRCapabilities.HeatStorage.BLOCK, serverLevel, getBlockPos().above(), Direction.DOWN);
        }
        super.onLoad();
    }

    public boolean isActive() {
        return this.burnTime > 0;
    }

    public int getProductionAmount() {
        return 3;
    }

    @Override
    public void onItemsChanged(int slot) {
        IItemHandler itemHandler = CapabilityUtils.itemHandlerCapability(this);
        if (itemHandler != null) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime > 0 && this.burnTime <= 0) {
                this.burnTime = burnTime;
                this.maxBurnTime = burnTime;
                stack.shrink(1);
                setBlockActive(true);
            }
        }
    }

    @Override
    public <T> Map<Direction, Pair<IOActions, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
        if (capability == Capabilities.ItemHandler.BLOCK) {
            return Map.of(
                    Direction.NORTH, Pair.of(IOActions.INSERT, new int[]{0}),
                    Direction.EAST, Pair.of(IOActions.INSERT, new int[]{0}),
                    Direction.SOUTH, Pair.of(IOActions.INSERT, new int[]{0}),
                    Direction.WEST, Pair.of(IOActions.INSERT, new int[]{0})
            );
        }
        return Map.of();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Firebox");
    }

    public void commonTick() {
        tickRecipe();
        tickIO();
    }

    private void tickIO() {
        if (!level.isClientSide()) {
            // Only export heat to block directly above
            BlockPos abovePos = worldPosition.above();
            if (aboveBlockCapCache != null) {
                IHeatStorage aboveHeatStorage = aboveBlockCapCache.getCapability();
                if (aboveHeatStorage != null && level != null) {
                    IHeatStorage thisHeatStorage = getHeatStorage();
                    int output = Math.min(thisHeatStorage.getMaxOutput(), aboveHeatStorage.getMaxInput());
                    int drained = thisHeatStorage.tryDrainHeat(output, true);
                    thisHeatStorage.tryDrainHeat(drained, false);
                    if (aboveHeatStorage.tryFillHeat(drained, false) != 0) {
                        level.invalidateCapabilities(abovePos);
                    }
                }
            }
        }
    }

    private void tickRecipe() {
        IItemHandler itemHandler = CapabilityUtils.itemHandlerCapability(this);
        IHeatStorage heatStorage = CapabilityUtils.heatStorageCapability(this);
        if (heatStorage != null) {
            if (this.burnTime > 0) {
                burnTime--;
                if (burnTime % 5 == 0) {
                    if (!level.isClientSide()) {
                        heatStorage.tryFillHeat(getProductionAmount(), false);
                    }
                }
            } else {
                this.maxBurnTime = 0;
                ItemStack stack = itemHandler.getStackInSlot(INPUT_SLOT);
                int burnTime = stack.getBurnTime(RecipeType.SMELTING);
                if (burnTime > 0) {
                    this.burnTime = burnTime;
                    this.maxBurnTime = burnTime;
                    stack.shrink(1);
                    setBlockActive(true);
                } else {
                    setBlockActive(false);
                }
            }
        }
    }

    public void setBlockActive(boolean value) {
        for (BlockPos pos : BlockUtils.getBlocksAroundSelfHorizontal(worldPosition)) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.hasProperty(FireboxPartBlock.HATCH_ACTIVE)) {
                level.setBlockAndUpdate(pos, blockState.setValue(FireboxPartBlock.HATCH_ACTIVE, value));
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
        return new FireBoxMenu(containerId, inventory, this);
    }

    @Override
    protected void saveData(CompoundTag pTag, HolderLookup.Provider provider) {
        pTag.putInt("burnTime", burnTime);
        pTag.putInt("maxBurnTime", maxBurnTime);
    }

    @Override
    protected void loadData(CompoundTag pTag, HolderLookup.Provider provider) {
        burnTime = pTag.getInt("burnTime");
        maxBurnTime = pTag.getInt("maxBurnTime");
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    public FireboxTier getFireboxTier() {
        return fireboxTier;
    }
}
