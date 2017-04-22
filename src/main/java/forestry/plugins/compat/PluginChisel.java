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
package forestry.plugins.compat;

import com.google.common.collect.ImmutableList;
import forestry.api.storage.BackpackManager;
import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@ForestryPlugin(pluginID = ForestryPluginUids.CHISEL, name = "Chisel", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.chisel.description")
public class PluginChisel extends BlankForestryPlugin {

	private static final String Chisel = "chisel";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(Chisel);
	}

	@Override
	public String getFailMessage() {
		return "Chisel not found";
	}

	@Override
	public void registerRecipes() {

		ImmutableList<String> worldgenBlocks = ImmutableList.of(
				"granite",
				"limestone",
				"marble",
				"andesite",
				"diorite"
		);
		for (String wBlocks : worldgenBlocks) {
			String message = String.format("%s@%s:%s", BackpackManager.DIGGER_UID, Chisel, wBlocks);
			FMLInterModComms.sendMessage(Constants.MOD_ID, "add-backpack-items", message);
		}

	}
}
