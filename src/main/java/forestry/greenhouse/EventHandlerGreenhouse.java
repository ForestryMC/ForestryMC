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
package forestry.greenhouse;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import forestry.api.core.ICamouflageHandler;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;

public class EventHandlerGreenhouse {
	
	@SubscribeEvent
	public void onCamouflageChance(CamouflageChangeEvent event) {
		ICamouflageHandler handler = event.camouflageHandler;
		
		IGreenhouseController controller = null;
		if (handler instanceof IGreenhouseController) {
			controller = (IGreenhouseController) handler;
		}
		
		if (handler instanceof IGreenhouseComponent) {
			IGreenhouseComponent component = (IGreenhouseComponent) handler;
			controller = component.getMultiblockLogic().getController();
		}
		
		if (controller != null) {
			controller.onChange(EnumGreenhouseEventType.CAMOUFLAGE, event);
		}
	}

}
