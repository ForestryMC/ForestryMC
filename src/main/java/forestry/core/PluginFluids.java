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
package forestry.core;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.LiquidRegistryHelper;
import forestry.core.items.EnumContainerType;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.FLUIDS, name = "Fluids", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.fluids.description")
public class PluginFluids extends BlankForestryPlugin {

	public static ItemRegistryFluids items;

	private static void createFluid(Fluids fluidDefinition) {
		if (fluidDefinition.getFluid() == null && Config.isFluidEnabled(fluidDefinition)) {
			String fluidName = fluidDefinition.getTag();
			if (!FluidRegistry.isFluidRegistered(fluidName)) {
				ResourceLocation[] resources = fluidDefinition.getResources();
				Fluid fluid = new Fluid(fluidName, resources[0], fluidDefinition.flowTextureExists() ? resources[1] : resources[0]);
				fluid.setDensity(fluidDefinition.getDensity());
				fluid.setViscosity(fluidDefinition.getViscosity());
				fluid.setTemperature(fluidDefinition.getTemperature());
				FluidRegistry.registerFluid(fluid);
				createBlock(fluidDefinition);
			}
		}
	}

	private static void createBlock(Fluids forestryFluid) {
		Fluid fluid = forestryFluid.getFluid();
		Block fluidBlock = fluid.getBlock();

		if (Config.isBlockEnabled(forestryFluid.getTag())) {
			if (fluidBlock == null) {
				fluidBlock = forestryFluid.makeBlock();
				if (fluidBlock != null) {
					String name = "fluid." + forestryFluid.getTag();
					fluidBlock.setUnlocalizedName("forestry." + name);
					GameRegistry.registerBlock(fluidBlock, ItemBlock.class, name);
					Proxies.render.registerFluidStateMapper(fluidBlock, forestryFluid);
					if (forestryFluid.getOtherContainers().isEmpty()) {
						FluidRegistry.addBucketForFluid(fluid);
					}
				}
			} else {
				ResourceLocation resourceLocation = GameData.getBlockRegistry().getNameForObject(fluidBlock);
				Log.warning("Pre-existing {} fluid block detected, deferring to {}:{}, "
						+ "this may cause issues if the server/client have different mod load orders, "
						+ "recommended that you disable all but one instance of {} fluid blocks via your configs.", fluid.getName(), resourceLocation.getResourceDomain(), resourceLocation.getResourcePath(), fluid.getName());
			}
		}
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public void registerItemsAndBlocks() {
		for (Fluids fluidType : Fluids.FORESTRY_FLUIDs) {
			createFluid(fluidType);
		}

		items = new ItemRegistryFluids();
	}

	@Override
	public void doInit() {
		for (Fluids fluidType : Fluids.values()) {
			if (fluidType.getFluid() == null) {
				continue;
			}

			for (EnumContainerType type : EnumContainerType.values()) {
				ItemLiquidContainer container = items.getContainer(type, fluidType);
				if (container == null) {
					continue;
				}

				LiquidRegistryHelper.registerLiquidContainer(fluidType, container.getItemStack());
			}

			for (ItemStack filledContainer : fluidType.getOtherContainers()) {
				LiquidRegistryHelper.registerLiquidContainer(fluidType, filledContainer);
			}
		}

		if (RecipeManagers.squeezerManager != null) {
			RecipeManagers.squeezerManager.addContainerRecipe(10, items.canEmpty.getItemStack(), PluginCore.items.ingotTin.getItemStack(), 0.05f);
			RecipeManagers.squeezerManager.addContainerRecipe(10, items.waxCapsuleEmpty.getItemStack(), PluginCore.items.beeswax.getItemStack(), 0.10f);
			RecipeManagers.squeezerManager.addContainerRecipe(10, items.refractoryEmpty.getItemStack(), PluginCore.items.refractoryWax.getItemStack(), 0.10f);
		}

		FluidStack ethanol = Fluids.BIO_ETHANOL.getFluid(1);
		GeneratorFuel ethanolFuel = new GeneratorFuel(ethanol, (int) (32 * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.generator")), 4);
		FuelManager.generatorFuel.put(ethanol.getFluid(), ethanolFuel);

		FluidStack biomass = Fluids.BIOMASS.getFluid(1);
		GeneratorFuel biomassFuel = new GeneratorFuel(biomass, (int) (8 * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.generator")), 1);
		FuelManager.generatorFuel.put(biomass.getFluid(), biomassFuel);
	}
}
