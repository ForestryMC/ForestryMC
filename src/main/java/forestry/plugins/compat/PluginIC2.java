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

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.farming.logic.FarmableBasicIC2Crop;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.energy.blocks.BlockEngineType;
import forestry.energy.blocks.BlockRegistryEnergy;
import forestry.energy.circuits.CircuitElectricBoost;
import forestry.energy.circuits.CircuitElectricChoke;
import forestry.energy.circuits.CircuitElectricEfficiency;
import forestry.energy.tiles.EngineDefinition;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicRubber;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginEnergy;
import forestry.plugins.PluginManager;

import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;

@Plugin(pluginID = "IC2", name = "IndustrialCraft2", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.ic2.description")
public class PluginIC2 extends ForestryPlugin {

	public static PluginIC2 instance;

	private static ItemStack plantBall;
	private static ItemStack compressedPlantBall;
	private static ItemStack wrench;
	private static ItemStack treetap;
	private static ItemStack rubbersapling;
	private static ItemStack rubberleaves;
	private static ItemStack emptyCell;
	private static ItemStack lavaCell;
	private static ItemStack waterCell;
	private static ItemStack rubber;
	private static ItemStack scrap;
	private static ItemStack silver;
	private static ItemStack brass;
	private static ItemStack uuMatter;

	public static ItemStack resin;
	public static ItemStack rubberwood;

	public PluginIC2() {
		if (PluginIC2.instance == null) {
			PluginIC2.instance = this;
		}
	}

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded("IC2");
	}

	@Override
	public String getFailMessage() {
		return "IndustrialCraft2 not found";
	}

	@Override
	public EnumSet<PluginManager.Module> getDependancies() {
		EnumSet<PluginManager.Module> deps = super.getDependancies();
		deps.add(PluginManager.Module.FARMING);
		deps.add(PluginManager.Module.FACTORY);
		return deps;
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void preInit() {
		super.preInit();

		BlockRegistryEnergy energyBlocks = PluginEnergy.blocks;
		if (energyBlocks != null) {
			energyBlocks.engine.addDefinitions(
					new EngineDefinition(BlockEngineType.ELECTRIC),
					new EngineDefinition(BlockEngineType.GENERATOR)
			);
		}

		emptyCell = IC2Items.getItem("cell");
		if (emptyCell != null) {
			lavaCell = IC2Items.getItem("lavaCell");
			waterCell = IC2Items.getItem("waterCell");
		} else {
			Log.fine("IC2 empty cell could not be found. Skipped adding IC2 liquid containers.");
		}

		// rubber chain
		treetap = IC2Items.getItem("treetap");
		rubberwood = IC2Items.getItem("rubberWood");
		resin = IC2Items.getItem("resin");
		rubbersapling = IC2Items.getItem("rubberSapling");
		rubberleaves = IC2Items.getItem("rubberLeaves");

		// fermentation
		plantBall = IC2Items.getItem("plantBall");
		compressedPlantBall = IC2Items.getItem("compressedPlantBall");

		// crated
		resin = IC2Items.getItem("resin");
		rubber = IC2Items.getItem("rubber");
		scrap = IC2Items.getItem("scrap");
		uuMatter = IC2Items.getItem("matter");
		silver = IC2Items.getItem("silverIngot");
		brass = IC2Items.getItem("bronzeIngot");

		Circuit.farmRubberManual = new CircuitFarmLogic("manualRubber", FarmLogicRubber.class);

		ICircuitLayout layoutEngineTin = new CircuitLayout("engine.tin", CircuitSocketType.ELECTRIC_ENGINE);
		ChipsetManager.circuitRegistry.registerLayout(layoutEngineTin);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void doInit() {
		super.doInit();

		// Remove some items from the recycler
		if (Recipes.recyclerBlacklist != null) {
			if (PluginManager.Module.APICULTURE.isEnabled()) {
				ItemRegistryApiculture beeItems = PluginApiculture.items;
				Recipes.recyclerBlacklist.add(new RecipeInputItemStack(new ItemStack(beeItems.beeQueenGE)));
				Recipes.recyclerBlacklist.add(new RecipeInputItemStack(new ItemStack(beeItems.beePrincessGE)));
			}
		} else {
			Log.severe("IC2 Recipes.recyclerBlacklist not found.");
		}

		Circuit.energyElectricChoke1 = new CircuitElectricChoke("electric.choke.1");
		Circuit.energyElectricEfficiency1 = new CircuitElectricEfficiency("electric.efficiency.1");
		Circuit.energyElectricBoost1 = new CircuitElectricBoost("electric.boost.1", 2, 7, 20);
		Circuit.energyElectricBoost2 = new CircuitElectricBoost("electric.boost.2", 2, 15, 40);
	}

	@Override
	@Optional.Method(modid = "IC2")
	protected void registerBackpackItems() {
		if (BackpackManager.definitions == null) {
			return;
		}

		IBackpackDefinition forester = BackpackManager.definitions.get("forester");

		if (resin != null) {
			forester.addValidItem(resin);
		}
		if (rubber != null) {
			forester.addValidItem(rubber);
		}
		if (rubbersapling != null) {
			forester.addValidItem(rubbersapling);
		}
		if (rubberleaves != null) {
			forester.addValidItem(rubberleaves);
		}
	}

	@Override
	@Optional.Method(modid = "IC2")
	protected void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		if (resin != null) {
			crateRegistry.registerCrate(resin, "cratedResin");
		}

		if (rubber != null) {
			crateRegistry.registerCrate(rubber, "cratedRubber");
		}

		if (scrap != null) {
			crateRegistry.registerCrate(scrap, "cratedScrap");
		}

		if (uuMatter != null) {
			crateRegistry.registerCrate(uuMatter, "cratedUUM");
		}

		if (silver != null) {
			crateRegistry.registerCrateUsingOreDict(silver, "cratedSilver");
		}

		if (brass != null) {
			crateRegistry.registerCrateUsingOreDict(brass, "cratedBrass");
		}
	}

	@Optional.Method(modid = "IC2")
	protected void registerRecipes() {

		if (rubber != null) {
			for (Object rubberOreDict : RecipeUtil.getOreDictRecipeEquivalents(rubber)) {
				RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), PluginCore.items.tubes.get(EnumElectronTube.RUBBER, 4),
						new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', rubberOreDict});
			}
		}

		if (plantBall != null) {
			RecipeUtil.addFermenterRecipes(plantBall, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat") * 9, Fluids.BIOMASS);
		}
		if (compressedPlantBall != null) {
			RecipeUtil.addFermenterRecipes(compressedPlantBall, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat") * 9, Fluids.BIOMASS);
		}

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			if (resin != null) {
				ItemRegistryApiculture beeItems = PluginApiculture.items;
				RecipeManagers.centrifugeManager.addRecipe(20, beeItems.propolis.get(EnumPropolis.NORMAL, 1), ImmutableMap.of(resin, 1.0f));
			} else {
				Log.fine("Missing IC2 resin, skipping centrifuge recipe for propolis to resin.");
			}
		}

		if (rubbersapling != null) {
			RecipeUtil.addFermenterRecipes(rubbersapling, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		} else {
			Log.fine("Missing IC2 rubber sapling, skipping fermenter recipe for converting rubber sapling to biomass.");
		}

		if (rubbersapling != null && resin != null) {
			String saplingName = GameData.getBlockRegistry().getNameForObject(ItemStackUtil.getBlock(rubbersapling));
			String resinName = GameData.getItemRegistry().getNameForObject(resin.getItem());
			String imc = String.format("farmArboreal@%s.%s.%s.%s",
					saplingName, rubbersapling.getItemDamage(),
					resinName, resin.getItemDamage());
			Log.finest("Sending IMC '%s'.", imc);
			FMLInterModComms.sendMessage(Constants.MOD, "add-farmable-sapling", imc);
		}

		FluidStack biogas = FluidRegistry.getFluidStack("ic2biogas", 1000);
		if (biogas != null && PluginManager.Module.ENERGY.isEnabled()) {
			FuelManager.bronzeEngineFuel.put(biogas.getFluid(), new EngineBronzeFuel(Fluids.BIOMASS.getFluid(),
					Constants.ENGINE_FUEL_VALUE_BIOMASS, (int) ((Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas"))), 1));
		}

		if (waterCell != null) {
			ItemStack bogEarthCan = ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.can");
			if (bogEarthCan.stackSize > 0) {
				RecipeUtil.addRecipe(bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', waterCell, 'Y', "sand");
			}
		}

		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.engine.tin");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.COPPER, 1), Circuit.energyElectricChoke1);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.TIN, 1), Circuit.energyElectricBoost1);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1), Circuit.energyElectricBoost2);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.IRON, 1), Circuit.energyElectricEfficiency1);
		
		if (PluginManager.Module.FARMING.isEnabled() && resin != null && rubberwood != null) {
			ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");
			ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.RUBBER, 1), Circuit.farmRubberManual);
		}

		Block ic2Crop = GameRegistry.findBlock("IC2", "blockCrop");
		if (PluginManager.Module.FARMING.isEnabled() && ic2Crop != null) {
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicIC2Crop());
		}

		BlockRegistryEnergy energyBlocks = PluginEnergy.blocks;
		if (energyBlocks != null) {
			RecipeUtil.addRecipe(energyBlocks.engine.get(BlockEngineType.GENERATOR),
					"X#X",
					"XYX",
					"X#X",
					'#', "blockGlass",
					'X', "ingotGold",
					'Y', PluginCore.items.sturdyCasing);

			RecipeUtil.addRecipe(energyBlocks.engine.get(BlockEngineType.ELECTRIC),
					"###",
					" X ",
					"YVY",
					'#', "ingotTin",
					'X', "blockGlass",
					'Y', "gearTin",
					'V', Blocks.piston);
		}
	}

	/**
	 * Check if there is an instance of ICropTile.
	 * @param tileEntity tile entity to be checked.
	 * @return true if there is an IC2 crop and false otherwise.
	 */
	@Optional.Method(modid = "IC2")
	public boolean isIC2Crop(TileEntity tileEntity) {
		if (tileEntity != null && tileEntity instanceof ICropTile) {
			return true;
		}
		return false;
	}

	/**
	 * Check if an IC2 crop is ready to be harvested.
	 * @param tileEntity tile entity to be checked.
	 * @return true if crop size is optimal for harvest and false otherwise.
	 */
	@Optional.Method(modid = "IC2")
	public boolean canHarvestCrop(TileEntity tileEntity) {
		if (isIC2Crop(tileEntity)) {
			ICropTile crop = (ICropTile)tileEntity;
			if (crop.getCrop() == null) {
				return false;
			}
			if (crop.getSize() == crop.getCrop().getOptimalHavestSize(crop)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Perform some of the actions of the crop-matron.
	 * @param tileEntity
	 */
	@Optional.Method(modid = "IC2")
	public void babysitCrop(TileEntity tileEntity) {
		if (isIC2Crop(tileEntity)) {
			ICropTile crop = (ICropTile)tileEntity;
			/*
			This part might be unbalanced until a custom farm logic is added and makes use of weed-ex.
			if (crop.getCrop() != null) {
				if (crop.getCrop().isWeed(crop)) {
					crop.reset();
				}
			}*/
			if (crop.getHydrationStorage() <= 200) {
				crop.setHydrationStorage(200);
			}
			if (crop.getNutrientStorage() <= 100) {
				crop.setNutrientStorage(crop.getNutrientStorage() + 100);
			}
		}
	}

	/**
	 * This function takes care of everything related to the harvesting of the
	 * crop meaning it will calculate the drops and also do setSizeAfterHarvest().
	 * @param tileEntity tile entity to be checked.
	 * @return arraylist containing the drops.
	 */
	@Optional.Method(modid = "IC2")
	public ArrayList<ItemStack> getCropDrops(TileEntity tileEntity) {
		if (isIC2Crop(tileEntity)) {
			ICropTile crop = (ICropTile)tileEntity;
			ItemStack[] cropDrops = crop.harvest_automated(true);
			if (cropDrops != null) {
				return new ArrayList<>(Arrays.asList(cropDrops));
			}
		}
		return null;
	}
}
