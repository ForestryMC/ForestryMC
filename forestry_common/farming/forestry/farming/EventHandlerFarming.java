/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming;

import net.minecraft.block.Block;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.event.entity.player.BonemealEvent;

import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.farming.gadgets.BlockMushroom;

public class EventHandlerFarming {

	@SubscribeEvent
	public void handleBonemeal(BonemealEvent event) {

		if (!Proxies.common.isSimulating(event.world))
			return;

		Block block = event.world.getBlock(event.x, event.y, event.z);
		if (block != ForestryBlock.mushroom)
			return;

		((BlockMushroom) ForestryBlock.mushroom).func_149878_d(event.world, event.x, event.y, event.z, event.world.rand);
		event.setResult(Result.ALLOW);
	}
}
