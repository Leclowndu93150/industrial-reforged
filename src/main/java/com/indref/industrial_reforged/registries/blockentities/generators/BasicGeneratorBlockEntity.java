package com.indref.industrial_reforged.registries.blockentities.generators;

import com.google.common.collect.ImmutableMap;
import com.indref.industrial_reforged.api.blockentities.generator.GeneratorBlockEntity;
import com.indref.industrial_reforged.api.blockentities.machine.MachineBlockEntity;
import com.indref.industrial_reforged.api.capabilities.IOActions;
import com.indref.industrial_reforged.api.capabilities.IRCapabilities;
import com.indref.industrial_reforged.api.capabilities.energy.IEnergyStorage;
import com.indref.industrial_reforged.transportation.energy.EnergyNet;
import com.indref.industrial_reforged.registries.data.saved.EnergyNetsSavedData;
import com.indref.industrial_reforged.registries.IRBlockEntityTypes;
import com.indref.industrial_reforged.registries.gui.menus.BasicGeneratorMenu;
import com.indref.industrial_reforged.tiers.EnergyTiers;
import com.indref.industrial_reforged.util.capabilities.CapabilityUtils;
import com.indref.industrial_reforged.util.EnergyNetUtils;
import com.indref.industrial_reforged.util.capabilities.SidedCapUtils;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class BasicGeneratorBlockEntity extends MachineBlockEntity implements MenuProvider, GeneratorBlockEntity {
    public static final int GENERATION_AMOUNT = 3;

    private int burnTime;
    private int maxBurnTime;

    public BasicGeneratorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(IRBlockEntityTypes.BASIC_GENERATOR.get(), p_155229_, p_155230_);
        addItemHandler(2, (slot, item) -> {
            boolean canInsertFuel = slot == 0 && item.getBurnTime(RecipeType.SMELTING) > 0;
            boolean canInsertBattery = slot == 1 && item.getCapability(IRCapabilities.EnergyStorage.ITEM) != null;
            return canInsertFuel || canInsertBattery;
        });
        addEnergyStorage(EnergyTiers.LOW);
    }

    public boolean isActive() {
        return this.burnTime > 0;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
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
            }
        }
    }

    @Override
    public <T> ImmutableMap<Direction, Pair<IOActions, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
        if (capability == Capabilities.ItemHandler.BLOCK) {
            return SidedCapUtils.allInsert(0);
        }
        return ImmutableMap.of();
    }

    @Override
    public void commonTick() {
        super.commonTick();
        IEnergyStorage energyStorage = getEnergyStorage();
        if (energyStorage != null) {
            if (this.burnTime > 0) {
                burnTime--;
                if (!level.isClientSide()) {
                    energyStorage.tryFillEnergy(GENERATION_AMOUNT, remove);
                }
            } else {
                this.maxBurnTime = 0;
                IItemHandler itemHandler = getItemHandler();
                ItemStack stack = itemHandler.getStackInSlot(0);
                int burnTime = stack.getBurnTime(RecipeType.SMELTING);
                if (burnTime > 0) {
                    this.burnTime = burnTime;
                    this.maxBurnTime = burnTime;
                    stack.shrink(1);
                }
            }
        }

        if (!level.isClientSide()) {
            IEnergyStorage thisEnergyStorage = CapabilityUtils.energyStorageCapability(this);
            if (level instanceof ServerLevel serverLevel) {
                EnergyNetsSavedData energyNets = EnergyNetUtils.getEnergyNets(serverLevel);
                Optional<EnergyNet> enet = energyNets.getEnets().getNetwork(worldPosition);
                if (enet.isPresent()) {
                    int filled = enet.get().distributeEnergy(Math.min(thisEnergyStorage.getEnergyTier().getMaxOutput(), thisEnergyStorage.getEnergyStored()));
                    thisEnergyStorage.tryDrainEnergy(filled, false);
                }
            }
        }
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

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("title.indref.basic_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BasicGeneratorMenu(i, inventory, this);
    }
}
