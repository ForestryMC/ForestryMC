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
