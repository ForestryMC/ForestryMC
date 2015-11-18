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
package forestry.apiculture.proxy;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;

import forestry.apiculture.entities.EntityBee;
import forestry.apiculture.render.RenderBeeEntity;
import forestry.apiculture.render.RenderBeeItem;
import forestry.core.render.IBlockRenderer;
import forestry.core.render.RenderAnalyzer;
import forestry.plugins.PluginApiculture;

public class ProxyApicultureClient extends ProxyApiculture {

	@Override
	public void initializeRendering() {
		if (PluginApiculture.fancyRenderedBees) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderBeeEntity());

			MinecraftForgeClient.registerItemRenderer(PluginApiculture.items.beeDroneGE, new RenderBeeItem());
			MinecraftForgeClient.registerItemRenderer(PluginApiculture.items.beePrincessGE, new RenderBeeItem());
			MinecraftForgeClient.registerItemRenderer(PluginApiculture.items.beeQueenGE, new RenderBeeItem());
		}
	}

	@Override
	public IBlockRenderer getRendererAnalyzer(String gfxBase) {
		return new RenderAnalyzer(gfxBase);
	}

}
