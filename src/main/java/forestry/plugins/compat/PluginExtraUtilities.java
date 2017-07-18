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
import java.util.function.Consumer;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
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
import forestry.farming.logic.FarmLogicExU;
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

	public static final String modId = "extrautils2";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(modId);
	}

	@Override
	public String getFailMessage() {
		return "ExtraUtilities not found";
	}

	@Override
	public void doInit() {
		super.doInit();

		if (Config.isExUtilRedOrchidEnabled()) {
			registerExPlant("Orchid", "redorchid", "Red Orchid", Blocks.REDSTONE_ORE, circuit -> Circuit.farmOrchidManaged = circuit);
		}

		if (Config.isExUtilEnderLilyEnabled()) {
			registerExPlant("Ender", "enderlilly", "Ender Lily", Blocks.END_STONE, circuit -> Circuit.farmEnderManaged = circuit);
		}
	}

	@Override
	public void registerRecipes() {
		super.registerRecipes();
		if (!ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) return;
		if(Circuit.farmEnderManaged != null) {
			ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
			ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.ENDER, 1), Circuit.farmEnderManaged);
		}
		if(Circuit.farmOrchidManaged != null) {
			ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
			ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.ORCHID, 1), Circuit.farmOrchidManaged);
		}
	}

	private void registerExPlant(String id, String itemResourceName, String itemName, Block soil, Consumer<ICircuit> assignTo) {
		Block plantBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(modId, itemResourceName));
		Item plantItem = Item.getItemFromBlock(plantBlock);
		if(plantBlock == Blocks.AIR) {
			Log.error("Could not find {} block.", itemName);
		} else if (plantItem == null) {
			Log.error("Could not find {} item.", itemName);
		} else {
			IProperty<Integer> growthProperty = BlockUtil.getProperty(plantBlock, "growth", Integer.class);
			if (growthProperty == null) {
				Log.error("Could not find the growth property of {}.", itemName);
			} else {
				int harvestAge = Collections.max(growthProperty.getAllowedValues());
				int replantAge = plantBlock.getDefaultState().getValue(growthProperty);
				Farmables.farmables.put(itemName, new FarmableAgingCrop(new ItemStack(plantItem), plantBlock, growthProperty, harvestAge, replantAge));
				assignTo.accept(new CircuitFarmLogic("managed" + id, new FarmLogicExU("Managed " + itemName + " Farm", plantItem, soil, itemName)));
			}
		}
	}
}
