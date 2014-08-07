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
package forestry.farming.proxy;

import net.minecraft.item.Item;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import forestry.core.config.ForestryBlock;
import forestry.core.utils.Localization;
import forestry.farming.render.FarmItemRenderer;
import forestry.farming.render.FarmRenderingHandler;
import forestry.plugins.PluginFarming;

public class ClientProxyFarming extends ProxyFarming {

	@Override
	public void initializeRendering() {
		PluginFarming.modelIdFarmBlock = RenderingRegistry.getNextAvailableRenderId();

		RenderingRegistry.registerBlockHandler(new FarmRenderingHandler());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ForestryBlock.farm), new FarmItemRenderer());
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/farming/");
	}

}
