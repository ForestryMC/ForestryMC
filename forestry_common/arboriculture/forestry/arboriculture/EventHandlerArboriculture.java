/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
