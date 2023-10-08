package com.indref.industrial_reforged.api.capabilities.energy;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

/**
 * Basic Capability Interface used for handling
 * methods related to the energy storage capability
 */
@AutoRegisterCapability
public interface IEnergyStorage {
    int getEnergyStored();

    int getMaxEnergy();

    void setEnergyStored(int value);
}
