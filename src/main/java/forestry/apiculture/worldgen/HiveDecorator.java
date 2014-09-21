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

import java.util.Random;

import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class HiveDecorator {

	@SuppressWarnings("rawtypes")
	public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_HIVES", new Class[0], new Object[0]);
	private static HiveDecorator instance;
	private final WorldGenHiveForest forest = new WorldGenHiveForest();
	private final WorldGenHiveJungle jungle = new WorldGenHiveJungle();
	private final WorldGenHiveMeadows meadows = new WorldGenHiveMeadows();
	private final WorldGenHiveParched parched = new WorldGenHiveParched();
	private final WorldGenHiveEnd end = new WorldGenHiveEnd();
	private final WorldGenHiveSnow snow = new WorldGenHiveSnow();
	private final WorldGenHiveSwamp swamp = new WorldGenHiveSwamp();

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
		genHive(world, rand, worldX, worldZ, 42, 3, forest);
		genHive(world, rand, worldX, worldZ, 62, 4, jungle);
		genHive(world, rand, worldX, worldZ, 42, 1, meadows);
		genHive(world, rand, worldX, worldZ, 42, 1, parched);
		genHive(world, rand, worldX, worldZ, 42, 4, end);
		genHive(world, rand, worldX, worldZ, 42, 2, snow);
		genHive(world, rand, worldX, worldZ, 42, 2, swamp);
	}

	private void genHive(World world, Random rand, int worldX, int worldZ, int height, int attempts, WorldGenHive gen) {
		for (int i = 0; i < attempts; i++) {
			int randPosX = worldX + rand.nextInt(16);
			int randPosY = height + rand.nextInt(50);
			int randPosZ = worldZ + rand.nextInt(16);
			if (gen.generate(world, rand, randPosX, randPosY, randPosZ))
				return;
		}
	}
}
