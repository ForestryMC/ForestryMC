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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmable;
import forestry.core.circuits.Circuit;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.utils.ModUtil;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicEnder;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "ExtraUtilities", name = "ExtraUtilities", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.extrautilities.description")
public class PluginExtraUtilities extends ForestryPlugin {

	private static final String ExU = "ExtraUtilities";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(ExU);
	}

	@Override
	public String getFailMessage() {
		return "ExtraUtilities not found";
	}

	@Override
	public void doInit() {
		super.doInit();

		Block exUEnderLilly = GameRegistry.findBlock(ExU, "plant/ender_lilly");
		Farmables.farmables.put("farmEnder", new ArrayList<IFarmable>());
		if (Config.isExUtilEnderLilyEnabled()) {
			Circuit.farmEnderManaged = new CircuitFarmLogic("managedEnder", FarmLogicEnder.class);
			Farmables.farmables.get("farmEnder").add(new FarmableGenericCrop(new ItemStack(exUEnderLilly, 1, 0), exUEnderLilly, 7));
		}
	}

	@Override
	protected void registerRecipes() {
		super.registerRecipes();

		if (PluginManager.Module.FARMING.isEnabled() && Config.isExUtilEnderLilyEnabled()) {
			ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
			ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.ENDER, 1), Circuit.farmEnderManaged);
		}
	}
}
