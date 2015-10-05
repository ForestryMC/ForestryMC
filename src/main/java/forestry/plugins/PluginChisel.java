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
package forestry.plugins;

import com.google.common.collect.ImmutableList;

import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@Plugin(pluginID = "Chisel", name = "Chisel", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.chisel.description")
public class PluginChisel extends ForestryPlugin {

	private static final String Chisel = "chisel";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(Chisel);
	}

	@Override
	public String getFailMessage() {
		return "Chisel not found";
	}

	@Override
	protected void registerRecipes() {

		ImmutableList<String> worldgenBlocks = ImmutableList.of(
				"granite",
				"limestone",
				"marble",
				"andesite",
				"diorite"
		);
		for (String wBlocks : worldgenBlocks) {
			FMLInterModComms.sendMessage(Defaults.MOD, "add-backpack-items", String.format("digger@%s:%s", Chisel, wBlocks));
		}

	}
}
