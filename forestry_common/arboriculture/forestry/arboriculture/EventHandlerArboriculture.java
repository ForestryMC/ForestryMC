/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.event.entity.player.BonemealEvent;

import forestry.api.genetics.IFruitBearer;
import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.proxy.Proxies;

public class EventHandlerArboriculture {

	@SubscribeEvent
	public void handleBonemeal(BonemealEvent event) {

		if (!Proxies.common.isSimulating(event.world))
			return;

		TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
		if (tile instanceof TileSapling) {
			int result = ((TileSapling) tile).tryGrow(true);
			if (result == 1 || result == 2)
				event.setResult(Result.ALLOW);
		} else if (tile instanceof IFruitBearer) {
			IFruitBearer bearer = (IFruitBearer) tile;
			if (bearer.getRipeness() <= 1.0f) {
				bearer.addRipeness(1.0f);
				event.setResult(Result.ALLOW);
			}
		} else if(tile instanceof TileFruitPod) {
			if(((TileFruitPod)tile).canMature()) {
				((TileFruitPod)tile).mature();
				event.setResult(Result.ALLOW);
			}
		}
	}

}
