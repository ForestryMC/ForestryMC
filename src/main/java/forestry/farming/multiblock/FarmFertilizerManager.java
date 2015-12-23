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

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.ForestryAPI;
import forestry.api.core.INBTTagable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;

public class FarmFertilizerManager implements INBTTagable, IStreamable {
	private static final int BUFFER_FERTILIZER = 200;

	private final int fertilizerValue;
	private int storedFertilizer;

	public FarmFertilizerManager() {
		this.fertilizerValue = ForestryAPI.activeMode.getIntegerSetting("farms.fertilizer.value");
	}

	public boolean hasFertilizer(int amount) {
		if (fertilizerValue < 0) {
			return true;
		}

		return storedFertilizer >= amount;
	}

	public void removeFertilizer(int amount) {
		if (fertilizerValue < 0) {
			return;
		}

		storedFertilizer -= amount;
		if (storedFertilizer < 0) {
			storedFertilizer = 0;
		}
	}

	public boolean maintainFertilizer(InventoryFarm inventory) {
		if (storedFertilizer <= BUFFER_FERTILIZER) {
			if (fertilizerValue < 0) {
				storedFertilizer += 2000;
			} else if (inventory.useFertilizer()) {
				storedFertilizer += fertilizerValue;
			}
		}

		return storedFertilizer > 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		storedFertilizer = data.getInteger("StoredFertilizer");
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		data.setInteger("StoredFertilizer", storedFertilizer);
	}

	public int getStoredFertilizerScaled(int scale) {
		if (storedFertilizer == 0) {
			return 0;
		}

		return (storedFertilizer * scale) / (fertilizerValue + BUFFER_FERTILIZER);
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(storedFertilizer);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		storedFertilizer = data.readVarInt();
	}
}
