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
package forestry.lepidopterology.proxy;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import forestry.core.config.ForestryItem;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.render.ButterflyItemRenderer;
import forestry.lepidopterology.render.RenderButterfly;

public class ClientProxyLepidopterology extends ProxyLepidopterology {

	@Override
	public void initializeRendering() {
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly.class, new RenderButterfly());
		MinecraftForgeClient.registerItemRenderer(ForestryItem.butterflyGE.item(), new ButterflyItemRenderer());
	}
}
