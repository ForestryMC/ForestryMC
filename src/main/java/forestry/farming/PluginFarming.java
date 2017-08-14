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
package forestry.farming;


import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.Forestry;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.core.PluginCore;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.farming.blocks.BlockMushroom;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.logic.FarmLogicCereal;
import forestry.farming.logic.FarmLogicCocoa;
import forestry.farming.logic.FarmLogicGourd;
import forestry.farming.logic.FarmLogicInfernal;
import forestry.farming.logic.FarmLogicMushroom;
import forestry.farming.logic.FarmLogicOrchard;
import forestry.farming.logic.FarmLogicPeat;
import forestry.farming.logic.FarmLogicReeds;
import forestry.farming.logic.FarmLogicSucculent;
import forestry.farming.logic.FarmLogicVegetable;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.farming.logic.FarmableGE;
import forestry.farming.logic.FarmableGourd;
import forestry.farming.logic.FarmableStacked;
import forestry.farming.logic.FarmableVanillaMushroom;
import forestry.farming.logic.FarmableVanillaSapling;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.farming.proxy.ProxyFarming;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;
import forestry.farming.triggers.FarmingTriggers;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.FARMING, name = "Farming", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.farming.description")
public class PluginFarming extends BlankForestryPlugin {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.farming.proxy.ProxyFarmingClient", serverSide = "forestry.farming.proxy.ProxyFarming")
	public static ProxyFarming proxy;

	@Nullable
	private static BlockRegistryFarming blocks;

	public static BlockRegistryFarming getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void setupAPI() {
		ForestryAPI.farmRegistry = FarmRegistry.getInstance();
	}

	@Override
	public void disabledSetupAPI() {
		ForestryAPI.farmRegistry = new DummyFarmRegistry();
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryFarming();
	}

	@Override
	public void preInit() {
		ItemRegistryCore coreItems = PluginCore.getItems();
		BlockRegistryFarming blocks = getBlocks();

		MinecraftForge.EVENT_BUS.register(this);
		IFarmRegistry registry = ForestryAPI.farmRegistry;
		
		registry.registerFarmables("farmArboreal", new FarmableVanillaSapling());
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			registry.registerFarmables("farmArboreal", new FarmableGE());
		}

		registry.registerFarmables("farmOrchard",
				new FarmableAgingCrop(new ItemStack(Items.WHEAT_SEEDS), Blocks.WHEAT, BlockCrops.AGE, 7, 0),
				new FarmableAgingCrop(new ItemStack(Items.POTATO), Blocks.POTATOES, BlockCrops.AGE, 7, 0),
				new FarmableAgingCrop(new ItemStack(Items.CARROT), Blocks.CARROTS, BlockCrops.AGE, 7, 0),
				new FarmableAgingCrop(new ItemStack(Items.BEETROOT_SEEDS), Blocks.BEETROOTS, BlockBeetroot.BEETROOT_AGE, 3, 0));

		IBlockState plantedBrownMushroom = blocks.mushroom.getDefaultState().withProperty(BlockMushroom.VARIANT, BlockMushroom.MushroomType.BROWN);
		registry.registerFarmables("farmShroom", new FarmableVanillaMushroom(new ItemStack(Blocks.BROWN_MUSHROOM), plantedBrownMushroom, Blocks.BROWN_MUSHROOM_BLOCK));

		IBlockState plantedRedMushroom = blocks.mushroom.getDefaultState().withProperty(BlockMushroom.VARIANT, BlockMushroom.MushroomType.RED);
		registry.registerFarmables("farmShroom", new FarmableVanillaMushroom(new ItemStack(Blocks.RED_MUSHROOM), plantedRedMushroom, Blocks.RED_MUSHROOM_BLOCK));

		registry.registerFarmables("farmWheat", new FarmableAgingCrop(new ItemStack(Items.WHEAT_SEEDS), Blocks.WHEAT, BlockCrops.AGE, 7));

		registry.registerFarmables("farmGourd", new FarmableGourd(new ItemStack(Items.PUMPKIN_SEEDS), Blocks.PUMPKIN_STEM, Blocks.PUMPKIN));
		registry.registerFarmables("farmGourd", new FarmableGourd(new ItemStack(Items.MELON_SEEDS), Blocks.MELON_STEM, Blocks.MELON_BLOCK));

		registry.registerFarmables("farmInfernal", new FarmableAgingCrop(new ItemStack(Items.NETHER_WART), Blocks.NETHER_WART, BlockNetherWart.AGE, 3));

		registry.registerFarmables("farmPoales", new FarmableStacked(new ItemStack(Items.REEDS), Blocks.REEDS, 3));

		registry.registerFarmables("farmSucculentes", new FarmableStacked(new ItemStack(Blocks.CACTUS), Blocks.CACTUS, 3));

		registry.registerFarmables("farmVegetables", new FarmableAgingCrop(new ItemStack(Items.POTATO), Blocks.POTATOES, BlockCrops.AGE, 7));
		registry.registerFarmables("farmVegetables", new FarmableAgingCrop(new ItemStack(Items.CARROT), Blocks.CARROTS, BlockCrops.AGE, 7));
		registry.registerFarmables("farmVegetables", new FarmableAgingCrop(new ItemStack(Items.BEETROOT_SEEDS), Blocks.BEETROOTS, BlockBeetroot.BEETROOT_AGE, 3));

		//Forestry fertilizer
		registry.registerFertilizer(new ItemStack(coreItems.fertilizerCompound, 1, OreDictionary.WILDCARD_VALUE), 500);

		proxy.initializeModels();

		// Layouts
		ICircuitLayout layoutManaged = new CircuitLayout("farms.managed", CircuitSocketType.FARM);
		ChipsetManager.circuitRegistry.registerLayout(layoutManaged);
		ICircuitLayout layoutManual = new CircuitLayout("farms.manual", CircuitSocketType.FARM);
		ChipsetManager.circuitRegistry.registerLayout(layoutManual);
	}

	@Override
	public void registerTriggers() {
		FarmingTriggers.initialize();
	}

	@Override
	public void doInit() {
		super.doInit();

		//Load config
		File configFile = new File(Forestry.instance.getConfigFolder(), Config.CATEGORY_FARM + ".cfg");
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");
		FarmRegistry.getInstance().loadConfig(config);
		config.save();

		GameRegistry.registerTileEntity(TileFarmPlain.class, "forestry.Farm");
		GameRegistry.registerTileEntity(TileFarmGearbox.class, "forestry.FarmGearbox");
		GameRegistry.registerTileEntity(TileFarmHatch.class, "forestry.FarmHatch");
		GameRegistry.registerTileEntity(TileFarmValve.class, "forestry.FarmValve");
		GameRegistry.registerTileEntity(TileFarmControl.class, "forestry.FarmControl");

		Circuits.farmArborealManaged = new CircuitFarmLogic("managedArboreal", new FarmLogicArboreal());
		Circuits.farmShroomManaged = new CircuitFarmLogic("managedShroom", new FarmLogicMushroom());
		Circuits.farmPeatManaged = new CircuitFarmLogic("managedPeat", new FarmLogicPeat());
		Circuits.farmCerealManaged = new CircuitFarmLogic("managedCereal", new FarmLogicCereal());
		Circuits.farmVegetableManaged = new CircuitFarmLogic("managedVegetable", new FarmLogicVegetable());
		Circuits.farmInfernalManaged = new CircuitFarmLogic("managedInfernal", new FarmLogicInfernal());

		Circuits.farmPeatManual = new CircuitFarmLogic("manualPeat", new FarmLogicPeat()).setManual();
		Circuits.farmShroomManual = new CircuitFarmLogic("manualShroom", new FarmLogicMushroom()).setManual();
		Circuits.farmCerealManual = new CircuitFarmLogic("manualCereal", new FarmLogicCereal()).setManual();
		Circuits.farmVegetableManual = new CircuitFarmLogic("manualVegetable", new FarmLogicVegetable()).setManual();
		Circuits.farmSucculentManual = new CircuitFarmLogic("manualSucculent", new FarmLogicSucculent()).setManual();
		Circuits.farmPoalesManual = new CircuitFarmLogic("manualPoales", new FarmLogicReeds()).setManual();
		Circuits.farmGourdManual = new CircuitFarmLogic("manualGourd", new FarmLogicGourd()).setManual();
		Circuits.farmCocoaManual = new CircuitFarmLogic("manualCocoa", new FarmLogicCocoa()).setManual();

		Circuits.farmOrchardManual = new CircuitFarmLogic("manualOrchard", new FarmLogicOrchard());
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = PluginCore.getItems();
		BlockRegistryFarming blocks = getBlocks();

		ItemStack basic = blocks.farm.get(EnumFarmBlockType.PLAIN, 1);
		ItemStack gearbox = blocks.farm.get(EnumFarmBlockType.GEARBOX, 1);
		ItemStack hatch = blocks.farm.get(EnumFarmBlockType.HATCH, 1);
		ItemStack valve = blocks.farm.get(EnumFarmBlockType.VALVE, 1);
		ItemStack control = blocks.farm.get(EnumFarmBlockType.CONTROL, 1);

		for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
			NBTTagCompound compound = new NBTTagCompound();
			block.saveToCompound(compound);

			basic.setTagCompound(compound);
			gearbox.setTagCompound(compound);
			hatch.setTagCompound(compound);
			valve.setTagCompound(compound);
			control.setTagCompound(compound);

			RecipeUtil.addRecipe("farm_basic_" + block.getUid(), basic,
					"I#I",
					"WCW",
					'#', block.getBase(),
					'W', "slabWood",
					'C', coreItems.tubes.get(EnumElectronTube.TIN, 1),
					'I', "ingotCopper");

			RecipeUtil.addRecipe("farm_gearbox_" + block.getUid(), gearbox,
					" # ",
					"TTT",
					'#', basic,
					'T', "gearTin");

			RecipeUtil.addRecipe("farm_hatch_" + block.getUid(), hatch,
					" # ",
					"TDT",
					'#', basic,
					'T', "gearTin",
					'D', OreDictUtil.TRAPDOOR_WOOD);

			RecipeUtil.addRecipe("farm_valve_" + block.getUid(), valve,
					" # ",
					"XTX",
					'#', basic,
					'T', "gearTin",
					'X', "blockGlass");

			RecipeUtil.addRecipe("farm_control_" + block.getUid(), control,
					" # ",
					"XTX",
					'#', basic,
					'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1),
					'X', "dustRedstone");
		}

		// Circuits
		ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
		ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");

		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.COPPER, 1), Circuits.farmArborealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuits.farmPeatManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuits.farmCerealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.IRON, 1), Circuits.farmVegetableManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.BLAZE, 1), Circuits.farmInfernalManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.APATITE, 1), Circuits.farmShroomManaged);

		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.COPPER, 1), Circuits.farmOrchardManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuits.farmPeatManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuits.farmCerealManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.IRON, 1), Circuits.farmVegetableManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.farmSucculentManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.DIAMOND, 1), Circuits.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.OBSIDIAN, 1), Circuits.farmGourdManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.APATITE, 1), Circuits.farmShroomManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.LAPIS, 1), Circuits.farmCocoaManual);
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// mushrooms are a workaround for the farm and should not be obtainable
		hiddenItems.add(new ItemStack(getBlocks().mushroom, 1, OreDictionary.WILDCARD_VALUE));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		EnumFarmBlockType.registerSprites();
	}
}
