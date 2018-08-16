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

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class FarmFertilizerManager implements INbtWritable, INbtReadable, IStreamable {
	private static final int BUFFER_FERTILIZER = 200;
	private int storedFertilizer;

	public FarmFertilizerManager() {
		storedFertilizer = 0;
	}

	public boolean hasFertilizer(IFarmInventoryInternal inventory, int amount) {
		if (inventory.getFertilizerValue() < 0) {
			return true;
		}

		return storedFertilizer >= amount;
	}

	public void removeFertilizer(IFarmInventoryInternal inventory, int amount) {
		if (inventory.getFertilizerValue() < 0) {
			return;
		}

		storedFertilizer -= amount;
		if (storedFertilizer < 0) {
			storedFertilizer = 0;
		}
	}

	public boolean maintainFertilizer(IFarmInventoryInternal inventory) {
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
	public void readFromNBT(NBTTagCompound data) {
		storedFertilizer = data.getInteger("StoredFertilizer");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data.setInteger("StoredFertilizer", storedFertilizer);
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
