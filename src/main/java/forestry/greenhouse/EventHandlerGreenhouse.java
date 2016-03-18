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

import forestry.api.core.CamouflageEvents.CamouflageChangeEvent;
import forestry.api.core.ICamouflageHandler;
import forestry.api.greenhouse.EnumGreenhouseChangeType;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandlerGreenhouse {
	
	@SubscribeEvent
	public void onCamouflageChance(CamouflageChangeEvent event){
		ICamouflageHandler handler = event.camouflageHandler;
		
		IGreenhouseController controller = null;
		if(handler instanceof IGreenhouseController){
			controller = (IGreenhouseController) handler;
		}
		
		if(handler instanceof IGreenhouseComponent){
			IGreenhouseComponent component = (IGreenhouseComponent) handler;
			controller = component.getMultiblockLogic().getController();
		}
		
		if(controller != null){
			controller.onChange(EnumGreenhouseChangeType.CAMOUFLAGE, event);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		BlockGreenhouseType.registerSprites();
	}

}
