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

import javax.annotation.Nonnull;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.BiomeHelper;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.tiles.IClimatised;
import forestry.farming.gui.IFarmLedgerDelegate;

public class FarmHydrationManager implements IFarmLedgerDelegate, INbtWritable, INbtReadable, IStreamable {
	private static final int DELAY_HYDRATION = 100;
	private static final float RAINFALL_MODIFIER_MAX = 15f;
	private static final float RAINFALL_MODIFIER_MIN = 0.5f;

	@Nonnull
	private final IClimatised climatised;
	private int hydrationDelay = 0;
	private int ticksSinceRainfall = 0;

	public FarmHydrationManager(@Nonnull IClimatised climatised) {
		this.climatised = climatised;
	}

	public void updateServer(World world, BlockPos coordinates) {
		if (world.isRainingAt(coordinates)) {
			if (hydrationDelay > 0) {
				hydrationDelay--;
			} else {
				ticksSinceRainfall = 0;
			}
		} else {
			hydrationDelay = DELAY_HYDRATION;
			if (ticksSinceRainfall < Integer.MAX_VALUE) {
				ticksSinceRainfall++;
			}
		}
	}

	@Override
	public float getHydrationModifier() {
		return getHydrationTempModifier() * getHydrationHumidModifier() * getHydrationRainfallModifier();
	}

	@Override
	public float getHydrationTempModifier() {
		float temperature = climatised.getExactTemperature();
		return temperature > 0.8f ? temperature : 0.8f;
	}

	@Override
	public float getHydrationHumidModifier() {
		float mod = 1 / climatised.getExactHumidity();
		return mod < 2.0f ? mod : 2.0f;
	}

	@Override
	public float getHydrationRainfallModifier() {
		float mod = (float) ticksSinceRainfall / 24000;
		if (mod <= RAINFALL_MODIFIER_MIN) {
			return RAINFALL_MODIFIER_MIN;
		} else if (mod >= RAINFALL_MODIFIER_MAX) {
			return RAINFALL_MODIFIER_MAX;
		} else {
			return mod;
		}
	}

	@Override
	public double getDrought() {
		return Math.round(((double) ticksSinceRainfall / 24000) * 10) / 10.;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("HydrationDelay", hydrationDelay);
		nbttagcompound.setInteger("TicksSinceRainfall", ticksSinceRainfall);
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(hydrationDelay);
		data.writeVarInt(ticksSinceRainfall);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		hydrationDelay = data.readVarInt();
		ticksSinceRainfall = data.readVarInt();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		hydrationDelay = nbt.getInteger("HydrationDelay");
		ticksSinceRainfall = nbt.getInteger("TicksSinceRainfall");
	}
}
