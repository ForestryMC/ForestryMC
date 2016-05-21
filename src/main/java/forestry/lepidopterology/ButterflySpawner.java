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

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.utils.Log;
import forestry.lepidopterology.entities.EntityButterfly;

public class ButterflySpawner implements ILeafTickHandler {

	@Override
	public boolean onRandomLeafTick(ITree tree, World world, Random rand, BlockPos pos, boolean isDestroyed) {
		
		if (rand.nextFloat() >= tree.getGenome().getSappiness() * tree.getGenome().getYield()) {
			return false;
		}
		
		IButterfly spawn = ButterflyManager.butterflyRoot.getIndividualTemplates().get(rand.nextInt(ButterflyManager.butterflyRoot.getIndividualTemplates().size()));
		float rarity;
		if(PluginLepidopterology.spawnRaritys.containsKey(spawn.getGenome().getPrimary().getUID())){
			rarity = spawn.getGenome().getPrimary().getRarity();
		}else{
			rarity = PluginLepidopterology.spawnRaritys.get(spawn.getGenome().getPrimary().getUID());
		}
		
		if (rand.nextFloat() >= rarity * 0.5f) {
			return false;
		}
		
		if (world.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint) {
			return false;
		}
		
		if (!spawn.canSpawn(world, pos.getX(), pos.getY(), pos.getZ())) {
			return false;
		}
		
		if (world.isAirBlock(pos.north())) {
			attemptButterflySpawn(world, spawn, pos.north());
		} else if (world.isAirBlock(pos.south())) {
			attemptButterflySpawn(world, spawn, pos.south());
		} else if (world.isAirBlock(pos.west())) {
			attemptButterflySpawn(world, spawn, pos.west());
		} else if (world.isAirBlock(pos.east())) {
			attemptButterflySpawn(world, spawn, pos.east());
		}
		
		return false;
	}

	private static void attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
		if (ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(), pos.getX(), pos.getY() + 0.1f, pos.getZ()) != null) {
			Log.trace("Spawned a butterfly '%s' at %s/%s/%s.", butterfly.getDisplayName(), pos.getX(), pos.getY(), pos.getZ());
		}
	}

}
