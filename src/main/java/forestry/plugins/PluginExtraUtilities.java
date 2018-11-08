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

import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.core.ModuleCore;
import forestry.core.circuits.Circuits;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.farming.FarmRegistry;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicRedOrchid;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.EXTRA_UTILITIES, name = "Extra Utilities", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.module.extrautilities.description")
public class PluginExtraUtilities extends CompatPlugin {

	public static ItemStack orchidStack = ItemStack.EMPTY;

	public PluginExtraUtilities() {
		super("Extra Utilities", "extrautils2");
	}

	@Override
	public void doInit() {
		Block redOrchid = getBlock("redorchid");
		Block enderLilly = getBlock("enderlilly");

		if (Config.isExUtilRedOrchidEnabled() && redOrchid != null) {
			Item item = Item.getItemFromBlock(redOrchid);

			registerFarmable(redOrchid, item, new ItemStack(Items.REDSTONE), ForestryFarmIdentifier.ORCHID);

			IFarmProperties orchidFarm = FarmRegistry.getInstance().registerLogic(ForestryFarmIdentifier.ORCHID, FarmLogicRedOrchid::new);
			orchidFarm.registerSoil(new ItemStack(Blocks.REDSTONE_ORE), Blocks.REDSTONE_ORE.getDefaultState());
			orchidFarm.registerSoil(new ItemStack(Blocks.LIT_REDSTONE_ORE), Blocks.LIT_REDSTONE_ORE.getDefaultState());

			Circuits.farmOrchidManaged = new CircuitFarmLogic("managedOrchid", orchidFarm, false);
			Circuits.farmOrchidManual = new CircuitFarmLogic("manualOrchid", orchidFarm, true);

			orchidStack = new ItemStack(item);
		}

		if (Config.isExUtilEnderLilyEnabled() && enderLilly != null) {
			Item item = Item.getItemFromBlock(enderLilly);
			registerFarmable(enderLilly, item, new ItemStack(Items.ENDER_PEARL), ForestryFarmIdentifier.ENDER);
		}
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING));
	}

	@Override
	public void registerRecipes() {
		if (Circuits.farmOrchidManaged != null) {
			ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
			ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");
			if (layoutManaged == null || layoutManual == null) {
				return;
			}
			ChipsetManager.solderManager.addRecipe(layoutManaged, ModuleCore.getItems().tubes.get(EnumElectronTube.ORCHID, 1), Circuits.farmOrchidManaged);
			ChipsetManager.solderManager.addRecipe(layoutManual, ModuleCore.getItems().tubes.get(EnumElectronTube.ORCHID, 1), Circuits.farmOrchidManual);
		}
	}

	private void registerFarmable(Block plantBlock, Item plantItem, ItemStack product, String identifier) {
		IProperty<Integer> growthProperty = BlockUtil.getProperty(plantBlock, "growth", Integer.class);
		if (growthProperty == null) {
			Log.error("Could not find the growth property of {}.", plantBlock.getLocalizedName());
		} else {
			IFarmRegistry registry = FarmRegistry.getInstance();
			int harvestAge = Collections.max(growthProperty.getAllowedValues());
			int replantAge = plantBlock.getDefaultState().getValue(growthProperty);
			registry.registerFarmables(identifier, new FarmableAgingCrop(new ItemStack(plantItem), plantBlock, product, growthProperty, harvestAge, replantAge));
		}
	}
}
