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

import javax.annotation.Nonnull;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.PluginCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.energy.PluginEnergy;
import forestry.energy.blocks.BlockRegistryEnergy;
import forestry.energy.circuits.CircuitElectricBoost;
import forestry.energy.circuits.CircuitElectricChoke;
import forestry.energy.circuits.CircuitElectricEfficiency;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicRubber;
import forestry.farming.logic.FarmableBasicIC2Crop;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@ForestryPlugin(pluginID = ForestryPluginUids.INDUSTRIALCRAFT2, name = "IndustrialCraft2", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.ic2.description")
public class PluginIC2 extends BlankForestryPlugin {
	public static final String modId = "IC2";
	public static PluginIC2 instance;

	private static ItemStack rubberSapling;
	private static ItemStack rubber;
	public static ItemStack rubberWood;
	public static ItemStack resin;

	public BlockRegistryIC2 blocks;

	public PluginIC2() {
		if (PluginIC2.instance == null) {
			PluginIC2.instance = this;
		}
	}

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(PluginIC2.modId);
	}

	@Override
	public String getFailMessage() {
		return "IndustrialCraft2 not found";
	}

	@Nonnull
	@Override
	public Set<String> getDependencyUids() {
		Set<String> dependencyUids = super.getDependencyUids();
		dependencyUids.add(ForestryPluginUids.FARMING);
		dependencyUids.add(ForestryPluginUids.FACTORY);
		return dependencyUids;
	}

	@Override
	@Optional.Method(modid = PluginIC2.modId)
	public void preInit() {
		super.preInit();

		// rubber chain
		rubberWood = IC2Items.getItem("rubber_wood");
		resin = IC2Items.getItem("misc_resource", "resin");
		rubberSapling = IC2Items.getItem("sapling");
		rubber = IC2Items.getItem("crafting", "rubber");

		Circuit.farmRubberManual = new CircuitFarmLogic("manualRubber", new FarmLogicRubber());

		ICircuitLayout layoutEngineTin = new CircuitLayout("engine.tin", CircuitSocketType.ELECTRIC_ENGINE);
		ChipsetManager.circuitRegistry.registerLayout(layoutEngineTin);
		
		Farmables.registerFertilizer(IC2Items.getItem("crop_res", "fertilizer"), 250);
	}

	@Override
	@Optional.Method(modid = PluginIC2.modId)
	public void doInit() {
		super.doInit();

		// Remove some items from the recycler
		if (Recipes.recyclerBlacklist != null) {
			ItemRegistryApiculture beeItems = PluginApiculture.items;
			if (beeItems != null) {
				Recipes.recyclerBlacklist.add(new RecipeInputItemStack(new ItemStack(beeItems.beeQueenGE)));
				Recipes.recyclerBlacklist.add(new RecipeInputItemStack(new ItemStack(beeItems.beePrincessGE)));
			}
		} else {
			Log.error("IC2 Recipes.recyclerBlacklist not found.");
		}

		Circuit.energyElectricChoke1 = new CircuitElectricChoke("electric.choke.1");
		Circuit.energyElectricEfficiency1 = new CircuitElectricEfficiency("electric.efficiency.1");
		Circuit.energyElectricBoost1 = new CircuitElectricBoost("electric.boost.1", 7, 20);
		Circuit.energyElectricBoost2 = new CircuitElectricBoost("electric.boost.2", 15, 40);

		blocks.electricalEngine.init();
		blocks.generator.init();
	}

	@Override
	public void postInit() {
		super.postInit();

		if (BackpackManager.backpackInterface == null) {
			return;
		}

		if (resin != null) {
			BackpackManager.backpackInterface.addItemToForestryBackpack(BackpackManager.FORESTER_UID, resin);
		}
		if (rubber != null) {
			BackpackManager.backpackInterface.addItemToForestryBackpack(BackpackManager.FORESTER_UID, rubber);
		}
		if (rubberSapling != null) {
			BackpackManager.backpackInterface.addItemToForestryBackpack(BackpackManager.FORESTER_UID, rubberSapling);
		}
		ItemStack rubberLeaves = IC2Items.getItem("leaves");
		if (rubberLeaves != null) {
			BackpackManager.backpackInterface.addItemToForestryBackpack(BackpackManager.FORESTER_UID, rubberLeaves);
		}
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		if (crateRegistry == null) {
			return;
		}

		if (resin != null) {
			crateRegistry.registerCrate(resin);
		}

		if (rubber != null) {
			crateRegistry.registerCrate(rubber);
		}

		ItemStack scrap = IC2Items.getItem("crafting", "scrap");
		if (scrap != null) {
			crateRegistry.registerCrate(scrap);
		}

		ItemStack uuMatter = IC2Items.getItem("misc_resource", "matter");
		if (uuMatter != null) {
			crateRegistry.registerCrate(uuMatter);
		}

		ItemStack silver = IC2Items.getItem("ingot", "silver");
		if (silver != null) {
			crateRegistry.registerCrate(silver);
		}

		ItemStack brass = IC2Items.getItem("ingot", "bronze");
		if (brass != null) {
			crateRegistry.registerCrate(brass);
		}
	}

	@Override
	@Optional.Method(modid = PluginIC2.modId)
	public void registerRecipes() {
		ItemRegistryCore coreItems = PluginCore.items;

		if (rubber != null) {
			for (Object rubberOreDict : RecipeUtil.getOreDictRecipeEquivalents(rubber)) {
				RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), coreItems.tubes.get(EnumElectronTube.RUBBER, 4),
						new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', rubberOreDict});
			}
		}

		ItemStack plantBall = IC2Items.getItem("crafting", "plant_ball");
		if (plantBall != null) {
			RecipeUtil.addFermenterRecipes(plantBall, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat") * 9, Fluids.BIOMASS);
		}

		ItemStack bioChaff = IC2Items.getItem("crafting", "bio_chaff");
		if (bioChaff != null) {
			RecipeUtil.addFermenterRecipes(bioChaff, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat") * 9, Fluids.BIOMASS);
		}

		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems != null) {
			if (resin != null) {
				RecipeManagers.centrifugeManager.addRecipe(20, beeItems.propolis.get(EnumPropolis.NORMAL, 1), ImmutableMap.of(resin, 1.0f));
			} else {
				Log.info("Missing IC2 resin, skipping centrifuge recipe for propolis to resin.");
			}
		}

		if (rubberSapling != null) {
			RecipeUtil.addFermenterRecipes(rubberSapling, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		} else {
			Log.info("Missing IC2 rubber sapling, skipping fermenter recipe for converting rubber sapling to biomass.");
		}

		if (rubberSapling != null && resin != null) {
			String saplingName = ItemStackUtil.getBlockNameFromRegistryAsSting(ItemStackUtil.getBlock(rubberSapling));
			String resinName = ItemStackUtil.getItemNameFromRegistryAsString(resin.getItem());
			String imc = String.format("farmArboreal@%s.%s.%s.%s",
					saplingName, rubberSapling.getItemDamage(),
					resinName, resin.getItemDamage());
			Log.trace("Sending IMC '%s'.", imc);
			FMLInterModComms.sendMessage(Constants.MOD_ID, "add-farmable-sapling", imc);
		}


		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ENERGY)) {
			Fluid biogas = FluidRegistry.getFluid("ic2biogas");
			if (biogas != null) {
				int burnDuration = Math.round(Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas"));
				EngineBronzeFuel bronzeFuel = new EngineBronzeFuel(Fluids.BIOMASS.getFluid(), Constants.ENGINE_FUEL_VALUE_BIOMASS, burnDuration, 1);
				FuelManager.bronzeEngineFuel.put(biogas, bronzeFuel);
			}
		}

		ItemStack waterCell = IC2Items.getItem("fluid_cell", "water");
		if (waterCell != null) {
			int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
			if (bogEarthOutputCan > 0) {
					ItemStack bogEarthCan = PluginCore.blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputCan);
				RecipeUtil.addRecipe(bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waterCell, 'Y', "sand");
			}
		}

		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.engine.tin");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.COPPER, 1), Circuit.energyElectricChoke1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuit.energyElectricBoost1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuit.energyElectricBoost2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.IRON, 1), Circuit.energyElectricEfficiency1);

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			if (resin != null && rubberWood != null) {
				ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");
				ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.RUBBER, 1), Circuit.farmRubberManual);
			}

			Farmables.farmables.get("farmOrchard").add(new FarmableBasicIC2Crop());
		}


		BlockRegistryEnergy energyBlocks = PluginEnergy.blocks;
		if (energyBlocks != null) {
			RecipeUtil.addRecipe(blocks.generator,
					"X#X",
					"XYX",
					"X#X",
					'#', "blockGlass",
					'X', "ingotGold",
					'Y', coreItems.sturdyCasing);

			RecipeUtil.addRecipe(blocks.electricalEngine,
					"###",
					" X ",
					"YVY",
					'#', "ingotTin",
					'X', "blockGlass",
					'Y', "gearTin",
					'V', Blocks.PISTON);
		}
		}

		@Override
		public void registerItemsAndBlocks() {
			super.registerItemsAndBlocks();
			blocks = new BlockRegistryIC2();
	}
}
