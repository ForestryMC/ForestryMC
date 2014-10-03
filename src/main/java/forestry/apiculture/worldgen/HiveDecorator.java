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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHive;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.Defaults;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class HiveDecorator {

	@SuppressWarnings("rawtypes")
	public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_HIVES", new Class[0], new Object[0]);
	private static HiveDecorator instance;

	public static HiveDecorator instance() {
		if (instance == null) {
			instance = new HiveDecorator();
		}
		return instance;
	}

	private HiveDecorator() {
	}

	@SubscribeEvent
	public void generate(PopulateChunkEvent.Post event) {
		if (!TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, EVENT_TYPE)) {
			return;
		}
		decorateHives(event.world, event.rand, event.chunkX * 16, event.chunkZ * 16);
	}

	private void decorateHives(World world, Random rand, int worldX, int worldZ) {
		for (IHive hive : HiveManager.getHives())
			genHive(world, rand, worldX, worldZ, hive);
	}

	private void genHive(World world, Random rand, int worldX, int worldZ, IHive hive) {
		if (hive.genChance() < rand.nextFloat() * 128.0f)
			return;

		BiomeGenBase biome = world.getBiomeGenForCoords(worldX, worldZ);
		EnumTemperature temperature = EnumTemperature.getFromValue(biome.temperature);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		if (!hive.isGoodClimate(biome, temperature, humidity))
			return;

		for (int tries = 0; tries < 4; tries ++) {
			int x = worldX + rand.nextInt(16);
			int z = worldZ + rand.nextInt(16);

			if (tryGenHive(world, x, z, hive))
				return;
		}
	}

	private boolean tryGenHive(World world, int x, int z, IHive hive) {

		int y = hive.getYForHive(world, x, z);

		if (y < 0)
			return false;

		if (!hive.canReplace(world, x, y, z))
			return false;

		if (!hive.isGoodLocation(world, x, y, z))
			return false;

		return setHive(world, x, y, z, hive);
	}

	protected boolean setHive(World world, int x, int y, int z, IHive hive) {
		Block hiveBlock = hive.getHiveBlock();
		boolean placed = world.setBlock(x, y, z, hiveBlock, hive.getHiveMeta(), Defaults.FLAG_BLOCK_SYNCH);
		if (!placed)
			return false;

		Block placedBlock = world.getBlock(x, y, z);
		if (!Block.isEqualTo(hiveBlock, placedBlock))
			return false;

		hiveBlock.onBlockAdded(world, x, y, z);
		world.markBlockForUpdate(x, y, z);

		hive.postGen(world, x, y, z);
		return true;
	}
}
