/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology;

import net.minecraft.world.World;

import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.proxy.Proxies;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.plugins.PluginLepidopterology;

public class ButterflySpawner implements ILeafTickHandler {

	@Override
	public boolean onRandomLeafTick(ITree tree, World world, int biomeId, int x, int y, int z, boolean isDestroyed) {
		
		if(world.rand.nextFloat() >= tree.getGenome().getSappiness() * tree.getGenome().getYield())
			return false;
		
		IButterfly spawn = PluginLepidopterology.butterflyInterface.getIndividualTemplates().get(world.rand.nextInt(PluginLepidopterology.butterflyInterface.getIndividualTemplates().size()));
		if(world.rand.nextFloat() >= spawn.getGenome().getPrimary().getRarity() * 0.5f)
			return false;
		
		if(world.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint)
			return false;
		
		if(!spawn.canSpawn(world, x, y, z))
			return false;
		
		if(world.isAirBlock(x - 1, y, z)) {
			attemptButterflySpawn(world, spawn, x - 1, y, z);
		} else if(world.isAirBlock(x + 1, y, z)) {
			attemptButterflySpawn(world, spawn, x + 1, y, z);
		} else if(world.isAirBlock(x, y, z - 1)) {
			attemptButterflySpawn(world, spawn, x, y, z - 1);
		} else if(world.isAirBlock(x, y, z + 1)) {
			attemptButterflySpawn(world, spawn, x, y, z + 1);
		}
		
		return false;
	}

	private void attemptButterflySpawn(World world, IButterfly butterfly, double x, double y, double z) {
		if(PluginLepidopterology.butterflyInterface.spawnButterflyInWorld(world, butterfly.copy(), x, y + 0.1f, z) != null)
			Proxies.log.finest("Spawned a butterfly '%s' at %s/%s/%s.", butterfly.getDisplayName(), x, y, z);
	}

}
