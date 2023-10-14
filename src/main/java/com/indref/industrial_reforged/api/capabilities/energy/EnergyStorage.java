package com.indref.industrial_reforged.api.capabilities.energy;

import com.indref.industrial_reforged.api.blocks.IEnergyBlock;
import com.indref.industrial_reforged.api.items.container.IEnergyContainerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Main file for handling storing and
 * modifying data of the energy capability
 * For the api look at
 * {@link IEnergyContainerItem} and
 * {@link IEnergyBlock}
 * <p>
 * Or use the {@link EnergyStorageProvider} and subscribe to the right {@link net.minecraftforge.event.AttachCapabilitiesEvent}
 */
public class EnergyStorage implements IEnergyStorage {
    public EnergyStorage() {
    }

    public EnergyStorage(ItemStack itemStack) {
        if (itemStack.getItem() instanceof IEnergyContainerItem energyItem) {
            this.capacity = energyItem.getCapacity(itemStack);
        }
    }

    public int stored;
    public int capacity;

    private static final String NBT_KEY_ENERGY_STORED = "storedEnergy";
    private static final String NBT_KEY_MAX_ENERGY = "maxEnergy";

    @Override
    public int getEnergyStored() {
        return this.stored;
    }

    @Override
    public int getEnergyCapacity() {
        return this.capacity;
    }

    @Override
    public void setEnergyStored(int value) {
        this.stored = value;
    }

    @Override
    public void setEnergyCapacity(int value) {
        this.capacity = value;
    }

    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_KEY_ENERGY_STORED, this.stored);
        tag.putInt(NBT_KEY_MAX_ENERGY, this.capacity);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.stored = nbt.getInt(NBT_KEY_ENERGY_STORED);
    }
}
