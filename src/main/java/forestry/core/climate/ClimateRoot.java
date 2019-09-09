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
package forestry.core.climate;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateProvider;
import forestry.api.climate.IClimateRoot;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IWorldClimateHolder;
import forestry.core.DefaultClimateProvider;

public class ClimateRoot implements IClimateRoot {

	private static final ClimateRoot INSTANCE = new ClimateRoot();

	public static ClimateRoot getInstance() {
		return INSTANCE;
	}

	@Nullable
	@Override
	public IClimateListener getListener(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null) {
			return tileEntity.getCapability(ClimateCapabilities.CLIMATE_LISTENER, null).orElse(null);
		}
		return null;
	}

	@Override
	public IClimateProvider getDefaultClimate(World world, BlockPos pos) {
		return new DefaultClimateProvider(world, pos);
	}

	@Override
	public IClimateState getState(World world, BlockPos pos) {
		IWorldClimateHolder climateHolder = getWorldClimate(world);
		return climateHolder.getState(pos);
	}

	public IClimateState getBiomeState(World worldObj, BlockPos coordinates) {
		Biome biome = worldObj.getBiome(coordinates);
		return ClimateStateHelper.of(biome.getTemperature(coordinates), biome.getDownfall());
	}

	@Override
	public IWorldClimateHolder getWorldClimate(World world) {
		//TODO - need to make sure this is only called server side...
		DimensionSavedDataManager storage = ((ServerWorld) world).getSavedData();
		WorldClimateHolder holder = storage.getOrCreate(() -> new WorldClimateHolder(WorldClimateHolder.NAME), WorldClimateHolder.NAME);
		holder.setWorld(world);
		return holder;
	}
}
