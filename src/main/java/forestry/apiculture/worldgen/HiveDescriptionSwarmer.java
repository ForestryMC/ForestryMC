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
package forestry.apiculture.worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.tiles.TileSwarm;
import forestry.plugins.PluginApiculture;

public class HiveDescriptionSwarmer implements IHiveDescription {

	private final ItemStack[] bees;

	public HiveDescriptionSwarmer(ItemStack... bees) {
		this.bees = bees;
	}

	@Override
	public IHiveGen getHiveGen() {
		return new HiveGenGround(Blocks.dirt, Blocks.grass);
	}

	@Override
	public Block getBlock() {
		return PluginApiculture.blocks.beehives;
	}

	@Override
	public int getMeta() {
		return 8;
	}

	@Override
	public boolean isGoodBiome(BiomeGenBase biome) {
		return true;
	}

	@Override
	public boolean isGoodHumidity(EnumHumidity humidity) {
		return true;
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		return true;
	}

	@Override
	public float getGenChance() {
		return 128.0f;
	}

	@Override
	public void postGen(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileSwarm) {
			((TileSwarm) tile).setContained(bees);
		}
	}
}
