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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.BlockForestryFluid;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.LiquidRegistryHelper;
import forestry.core.items.EnumContainerType;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.core.utils.StringUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.FLUIDS, name = "Fluids", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.fluids.description")
public class PluginFluids extends BlankForestryPlugin {

	private static final List<Fluids> forestryFluidsWithBlocks = new ArrayList<>();

	public static ItemRegistryFluids items;

	private static void createFluid(Fluids forestryFluid) {
		if (forestryFluid.getFluid() == null && Config.isFluidEnabled(forestryFluid)) {
			String fluidName = forestryFluid.getTag();
			if (!FluidRegistry.isFluidRegistered(fluidName)) {
				Fluid fluid = new Fluid(fluidName,
						forestryFluid.getResources()[0], forestryFluid.flowTextureExists() ? forestryFluid.getResources()[1]
						: forestryFluid.getResources()[0]).setDensity(forestryFluid.getDensity()).setViscosity(forestryFluid.getViscosity()).setTemperature(forestryFluid.getTemperature());
				FluidRegistry.registerFluid(fluid);
				createBlock(forestryFluid);
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
					fluidBlock.setUnlocalizedName("forestry.fluid." + forestryFluid.getTag());
					GameRegistry.registerBlock(fluidBlock, ItemBlock.class, StringUtil.cleanBlockName(fluidBlock));
					forestryFluidsWithBlocks.add(forestryFluid);
					Proxies.render.registerFluidStateMapper(fluidBlock, forestryFluid);
				}
			} else {
				GameRegistry.UniqueIdentifier blockID = GameRegistry.findUniqueIdentifierFor(fluidBlock);
				Log.warning("Pre-existing {} fluid block detected, deferring to {}:{}, "
						+ "this may cause issues if the server/client have different mod load orders, "
						+ "recommended that you disable all but one instance of {} fluid blocks via your configs.", fluid.getName(), blockID.modId, blockID.name, fluid.getName());
			}
		}
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public void registerItemsAndBlocks() {
		for (Fluids fluidType : Fluids.forestryFluids) {
			createFluid(fluidType);
		}

		items = new ItemRegistryFluids();
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(getFillBucketHook());
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

		FluidStack ethanol = Fluids.ETHANOL.getFluid(1);
		GeneratorFuel ethanolFuel = new GeneratorFuel(ethanol, (int) (32 * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.generator")), 4);
		FuelManager.generatorFuel.put(ethanol.getFluid(), ethanolFuel);

		FluidStack biomass = Fluids.BIOMASS.getFluid(1);
		GeneratorFuel biomassFuel = new GeneratorFuel(biomass, (int) (8 * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.generator")), 1);
		FuelManager.generatorFuel.put(biomass.getFluid(), biomassFuel);
	}

	public static class FillBucketHook {
		@SubscribeEvent
		public void fillBucket(FillBucketEvent event) {
			MovingObjectPosition movingObjectPosition = event.target;
			Block targetedBlock = BlockUtil.getBlock(event.world, event.target.getBlockPos());
			if (targetedBlock instanceof BlockForestryFluid) {
				Item filledBucket = ItemLiquidContainer.getExistingBucket(targetedBlock);
				if (filledBucket != null) {
					event.result = new ItemStack(filledBucket);
					event.setResult(Event.Result.ALLOW);
					if (!event.world.isRemote) {
						event.world.setBlockToAir(event.target.getBlockPos());
					}
				}
			}
		}
	}

	private static Object getFillBucketHook() {
		return new FillBucketHook();
	}
}
