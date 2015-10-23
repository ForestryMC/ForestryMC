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
package forestry.lepidopterology;

import net.minecraft.world.World;

import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.utils.Log;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.plugins.PluginLepidopterology;

public class ButterflySpawner implements ILeafTickHandler {

	@Override
	public boolean onRandomLeafTick(ITree tree, World world, int x, int y, int z, boolean isDestroyed) {
		
		if (world.rand.nextFloat() >= tree.getGenome().getSappiness() * tree.getGenome().getYield()) {
			return false;
		}
		
		IButterfly spawn = ButterflyManager.butterflyRoot.getIndividualTemplates().get(world.rand.nextInt(ButterflyManager.butterflyRoot.getIndividualTemplates().size()));
		if (world.rand.nextFloat() >= spawn.getGenome().getPrimary().getRarity() * 0.5f) {
			return false;
		}
		
		if (world.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint) {
			return false;
		}
		
		if (!spawn.canSpawn(world, x, y, z)) {
			return false;
		}
		
		if (world.isAirBlock(x - 1, y, z)) {
			attemptButterflySpawn(world, spawn, x - 1, y, z);
		} else if (world.isAirBlock(x + 1, y, z)) {
			attemptButterflySpawn(world, spawn, x + 1, y, z);
		} else if (world.isAirBlock(x, y, z - 1)) {
			attemptButterflySpawn(world, spawn, x, y, z - 1);
		} else if (world.isAirBlock(x, y, z + 1)) {
			attemptButterflySpawn(world, spawn, x, y, z + 1);
		}
		
		return false;
	}

	private static void attemptButterflySpawn(World world, IButterfly butterfly, double x, double y, double z) {
		if (ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(), x, y + 0.1f, z) != null) {
			Log.finest("Spawned a butterfly '%s' at %s/%s/%s.", butterfly.getDisplayName(), x, y, z);
		}
	}

}
