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
package forestry.energy.proxy;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.energy.render.RenderEngine;
import forestry.energy.tiles.TileEngine;
import forestry.modules.IClientModuleHandler;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyEnergyClient extends ProxyEnergy implements IClientModuleHandler {
	@Override
	public void setRenderDefaultEngine(MachinePropertiesTesr<? extends TileEngine> machineProperties, String baseTexture) {
		machineProperties.setRenderer(ctx -> new RenderEngine(ctx, baseTexture));
	}
	
	@Override
	public void setupLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(RenderEngine.ENGINE, RenderEngine::createBodyLayer);
	}
}
