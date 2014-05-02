/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.world.World;

public interface ILeafTickHandler {
	boolean onRandomLeafTick(ITree tree, World world, int biomeId, int x, int y, int z, boolean isDestroyed);
}
