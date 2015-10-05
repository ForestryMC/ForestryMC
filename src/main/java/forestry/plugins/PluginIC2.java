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

import com.google.common.collect.ImmutableMap;

import java.util.EnumSet;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameData;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.GameMode;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.energy.circuits.CircuitElectricBoost;
import forestry.energy.circuits.CircuitElectricChoke;
import forestry.energy.circuits.CircuitElectricEfficiency;
import forestry.energy.circuits.CircuitFireDampener;
import forestry.energy.gadgets.EngineDefinition;
import forestry.energy.gadgets.EngineTin;
import forestry.energy.gadgets.MachineGenerator;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicRubber;

import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;

@Plugin(pluginID = "IC2", name = "IndustrialCraft2", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.ic2.description")
public class PluginIC2 extends ForestryPlugin {

	public static PluginIC2 instance;

	// Forestry stuff
	private static MachineDefinition definitionGenerator;
	private static MachineDefinition definitionEngineTin;

	// IC2 stuff
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
		return Proxies.common.isModLoaded("IC2");
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
		deps.add(PluginManager.Module.ENERGY);
		return deps;
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void preInit() {
		super.preInit();

		definitionEngineTin = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINETIN_META, "forestry.EngineTin", EngineTin.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_tin_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINETIN_META),
				"###",
				" X ",
				"YVY",
				'#', "ingotTin",
				'X', "blockGlass",
				'Y', "gearTin",
				'V', Blocks.piston)));

		definitionGenerator = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_GENERATOR_META, "forestry.Generator", MachineGenerator.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/generator_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_GENERATOR_META),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotGold",
				'Y', ForestryItem.sturdyCasing)));

		emptyCell = IC2Items.getItem("cell");
		if (emptyCell != null) {
			lavaCell = IC2Items.getItem("lavaCell");
			waterCell = IC2Items.getItem("waterCell");
		} else {
			Proxies.log.fine("IC2 empty cell could not be found. Skipped adding IC2 liquid containers.");
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

		ICircuitLayout layoutEngineTin = new CircuitLayout("engine.tin");
		ChipsetManager.circuitRegistry.registerLayout(layoutEngineTin);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void doInit() {
		super.doInit();

		// Remove some items from the recycler
		if (Recipes.recyclerBlacklist != null) {
			if (PluginManager.Module.APICULTURE.isEnabled()) {
				Recipes.recyclerBlacklist.add(new RecipeInputItemStack(ForestryItem.beeQueenGE.getItemStack()));
				Recipes.recyclerBlacklist.add(new RecipeInputItemStack(ForestryItem.beePrincessGE.getItemStack()));
			}
		} else {
			Proxies.log.severe("IC2 Recipes.recyclerBlacklist not found.");
		}

		definitionEngineTin.register();
		definitionGenerator.register();

		Circuit.energyElectricChoke1 = new CircuitElectricChoke("electric.choke.1");
		Circuit.energyFireDampener1 = new CircuitFireDampener("dampener.1");
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

	@Override
	@Optional.Method(modid = "IC2")
	protected void registerRecipes() {

		if (rubber != null) {
			for (Object rubberOreDict : RecipeUtil.getOreDictRecipeEquivalents(rubber)) {
				RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 8),
						new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', rubberOreDict});
			}
		}

		if (plantBall != null) {
			RecipeUtil.injectLeveledRecipe(plantBall, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat") * 4, Fluids.BIOMASS);
		}
		if (compressedPlantBall != null) {
			RecipeUtil.injectLeveledRecipe(compressedPlantBall, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat") * 5, Fluids.BIOMASS);
		}

		if (resin != null) {
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.propolis.getItemStack(), ImmutableMap.of(resin, 1.0f));
		} else {
			Proxies.log.fine("Missing IC2 resin, skipping centrifuge recipe for propolis to resin.");
		}

		if (rubbersapling != null) {
			RecipeUtil.injectLeveledRecipe(rubbersapling, GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		} else {
			Proxies.log.fine("Missing IC2 rubber sapling, skipping fermenter recipe for converting rubber sapling to biomass.");
		}

		if (rubbersapling != null && resin != null) {
			String saplingName = GameData.getBlockRegistry().getNameForObject(StackUtils.getBlock(rubbersapling)).toString();
			String resinName = GameData.getItemRegistry().getNameForObject(resin.getItem()).toString();
			String imc = String.format("farmArboreal@%s.%s.%s.%s",
					saplingName, rubbersapling.getItemDamage(),
					resinName, resin.getItemDamage());
			Proxies.log.finest("Sending IMC '%s'.", imc);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", imc);
		}

		FluidStack biogas = FluidRegistry.getFluidStack("ic2biogas", 1000);
		if (biogas != null && PluginManager.Module.ENERGY.isEnabled()) {
			FuelManager.bronzeEngineFuel.put(biogas.getFluid(), new EngineBronzeFuel(Fluids.BIOMASS.getFluid(),
					Defaults.ENGINE_FUEL_VALUE_BIOMASS, (int) ((Defaults.ENGINE_CYCLE_DURATION_BIOMASS * GameMode.getGameMode().getFloatSetting("fuel.biomass.biogas"))), 1));
		}
		if (lavaCell != null) {
			LiquidHelper.injectTinContainer(Fluids.LAVA, Defaults.BUCKET_VOLUME, lavaCell, emptyCell);
		}

		if (waterCell != null) {
			LiquidHelper.injectTinContainer(Fluids.WATER, Defaults.BUCKET_VOLUME, waterCell, emptyCell);

			ItemStack bogEarthCan = GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can");
			if (bogEarthCan.stackSize > 0) {
				Proxies.common.addRecipe(bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', waterCell, 'Y', "sand");
			}
		}

		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.engine.tin");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 0), Circuit.energyElectricChoke1);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 1), Circuit.energyElectricBoost1);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 2), Circuit.energyElectricBoost2);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 3), Circuit.energyElectricEfficiency1);
		
		if (PluginManager.Module.FARMING.isEnabled() && resin != null && rubberwood != null) {
			ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");
			ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 8), Circuit.farmRubberManual);
		}
	}

}
