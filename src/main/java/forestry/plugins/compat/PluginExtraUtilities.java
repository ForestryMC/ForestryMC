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

import java.util.Collections;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.core.PluginCore;
import forestry.core.circuits.Circuit;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicEnder;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@ForestryPlugin(pluginID = ForestryPluginUids.EXTRA_UTILITIES, name = "ExtraUtilities", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.extrautilities.description")
public class PluginExtraUtilities extends BlankForestryPlugin {

	private static final String ExU = "ExtraUtils2";

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

		if (Config.isExUtilEnderLilyEnabled()) {
			Block enderLillyBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("extrautils2", "EnderLilly"));
			Item enderLillyItem = Item.getItemFromBlock(enderLillyBlock);
			if (enderLillyBlock == Blocks.AIR) {
				Log.error("Could not find ender lilly block.");
			} else if (enderLillyItem == null) {
				Log.error("Could not find ender lilly item.");
			} else {
				IProperty<Integer> growthProperty = BlockUtil.getProperty(enderLillyBlock, "growth", Integer.class);
				if (growthProperty == null) {
					Log.error("Could not find the growth property of ender lily.");
				} else {
					int harvestAge = Collections.max(growthProperty.getAllowedValues());
					int replantAge = enderLillyBlock.getDefaultState().getValue(growthProperty);
					Farmables.farmables.put("farmEnder", new FarmableAgingCrop(new ItemStack(enderLillyItem), enderLillyBlock, growthProperty, harvestAge, replantAge));
					Circuit.farmEnderManaged = new CircuitFarmLogic("managedEnder", new FarmLogicEnder());
				}
			}
		}
	}

	@Override
	public void registerRecipes() {
		super.registerRecipes();
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && Circuit.farmEnderManaged != null) {
			ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
			ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.ENDER, 1), Circuit.farmEnderManaged);
		}
	}
}
