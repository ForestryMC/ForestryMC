/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.multiblock;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.cultivation.IFarmHousingInternal;
import net.minecraft.nbt.CompoundNBT;

public class FarmFertilizerManager implements INbtWritable, INbtReadable, IStreamable {
    private static final int BUFFER_FERTILIZER = 200;
    private final IFarmInventoryInternal inventory;
    private int storedFertilizer;

    public FarmFertilizerManager(IFarmHousingInternal housing) {
        this.inventory = housing.getFarmInventory();
        storedFertilizer = 0;
    }

    public boolean hasFertilizer(int amount) {
        if (inventory.getFertilizerValue() < 0) {
            return true;
        }

        return storedFertilizer >= amount;
    }

    public void removeFertilizer(int amount) {
        if (inventory.getFertilizerValue() < 0) {
            return;
        }

        storedFertilizer -= amount;
        if (storedFertilizer < 0) {
            storedFertilizer = 0;
        }
    }

    public boolean maintainFertilizer() {
        if (storedFertilizer <= BUFFER_FERTILIZER) {
            int fertilizerValue = inventory.getFertilizerValue();
            if (fertilizerValue < 0) {
                storedFertilizer += 2000;
            } else if (inventory.useFertilizer()) {
                storedFertilizer += fertilizerValue;
            }
        }

        return storedFertilizer > 0;
    }

    @Override
    public void read(CompoundNBT data) {
        storedFertilizer = data.getInt("StoredFertilizer");
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        data.putInt("StoredFertilizer", storedFertilizer);
        return data;
    }

    public int getStoredFertilizerScaled(IFarmInventoryInternal inventory, int scale) {
        if (storedFertilizer == 0) {
            return 0;
        }

        return storedFertilizer * scale / (inventory.getFertilizerValue() + BUFFER_FERTILIZER);
    }

    @Override
    public void writeData(PacketBufferForestry data) {
        data.writeVarInt(storedFertilizer);
    }

    @Override
    public void readData(PacketBufferForestry data) {
        storedFertilizer = data.readVarInt();
    }
}
