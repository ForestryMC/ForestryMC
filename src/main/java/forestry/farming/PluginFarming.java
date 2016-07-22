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

import java.util.Arrays;
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
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.farming.Fertilizers;
import forestry.core.PluginCore;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.items.EnumElectronTube;
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

	@SidedProxy(clientSide = "forestry.farming.proxy.ProxyFarmingClient", serverSide = "forestry.farming.proxy.ProxyFarming")
	public static ProxyFarming proxy;
	public static int modelIdFarmBlock;

	public static BlockRegistryFarming blocks;

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryFarming();
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);

		Farmables.farmables.put("farmArboreal", new FarmableVanillaSapling());
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			Farmables.farmables.put("farmArboreal", new FarmableGE());
		}

		Farmables.farmables.putAll("farmOrchard", Arrays.asList(
				new FarmableAgingCrop(new ItemStack(Items.WHEAT_SEEDS), Blocks.WHEAT, BlockCrops.AGE, 7),
				new FarmableAgingCrop(new ItemStack(Items.POTATO), Blocks.POTATOES, BlockCrops.AGE, 7),
				new FarmableAgingCrop(new ItemStack(Items.CARROT), Blocks.CARROTS, BlockCrops.AGE, 7),
				new FarmableAgingCrop(new ItemStack(Items.BEETROOT_SEEDS), Blocks.BEETROOTS, BlockBeetroot.BEETROOT_AGE, 3)
		));

		IBlockState plantedBrownMushroom = blocks.mushroom.getDefaultState().withProperty(BlockMushroom.VARIANT, BlockMushroom.MushroomType.BROWN);
		Farmables.farmables.put("farmShroom", new FarmableVanillaMushroom(new ItemStack(Blocks.BROWN_MUSHROOM), plantedBrownMushroom, Blocks.BROWN_MUSHROOM_BLOCK));

		IBlockState plantedRedMushroom = blocks.mushroom.getDefaultState().withProperty(BlockMushroom.VARIANT, BlockMushroom.MushroomType.RED);
		Farmables.farmables.put("farmShroom", new FarmableVanillaMushroom(new ItemStack(Blocks.RED_MUSHROOM), plantedRedMushroom, Blocks.RED_MUSHROOM_BLOCK));

		Farmables.farmables.put("farmWheat", new FarmableAgingCrop(new ItemStack(Items.WHEAT_SEEDS), Blocks.WHEAT, BlockCrops.AGE, 7));

		Farmables.farmables.put("farmGourd", new FarmableGourd(new ItemStack(Items.PUMPKIN_SEEDS), Blocks.PUMPKIN_STEM, Blocks.PUMPKIN));
		Farmables.farmables.put("farmGourd", new FarmableGourd(new ItemStack(Items.MELON_SEEDS), Blocks.MELON_STEM, Blocks.MELON_BLOCK));

		Farmables.farmables.put("farmInfernal", new FarmableAgingCrop(new ItemStack(Items.NETHER_WART), Blocks.NETHER_WART, BlockNetherWart.AGE, 3));

		Farmables.farmables.put("farmPoales", new FarmableStacked(new ItemStack(Items.REEDS), Blocks.REEDS, 3));

		Farmables.farmables.put("farmSucculentes", new FarmableStacked(new ItemStack(Blocks.CACTUS), Blocks.CACTUS, 3));

		Farmables.farmables.put("farmVegetables", new FarmableAgingCrop(new ItemStack(Items.POTATO), Blocks.POTATOES, BlockCrops.AGE, 7));
		Farmables.farmables.put("farmVegetables", new FarmableAgingCrop(new ItemStack(Items.CARROT), Blocks.CARROTS, BlockCrops.AGE, 7));
		Farmables.farmables.put("farmVegetables", new FarmableAgingCrop(new ItemStack(Items.BEETROOT_SEEDS), Blocks.BEETROOTS, BlockBeetroot.BEETROOT_AGE, 3));

		// Fertilizers
		Fertilizers.fertilizers.put(PluginCore.items.fertilizerCompound, ForestryAPI.activeMode.getIntegerSetting("farms.fertilizer.value"));

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

		GameRegistry.registerTileEntity(TileFarmPlain.class, "forestry.Farm");
		GameRegistry.registerTileEntity(TileFarmGearbox.class, "forestry.FarmGearbox");
		GameRegistry.registerTileEntity(TileFarmHatch.class, "forestry.FarmHatch");
		GameRegistry.registerTileEntity(TileFarmValve.class, "forestry.FarmValve");
		GameRegistry.registerTileEntity(TileFarmControl.class, "forestry.FarmControl");

		Circuit.farmArborealManaged = new CircuitFarmLogic("managedArboreal", new FarmLogicArboreal());
		Circuit.farmShroomManaged = new CircuitFarmLogic("managedShroom", new FarmLogicMushroom());
		Circuit.farmPeatManaged = new CircuitFarmLogic("managedPeat", new FarmLogicPeat());
		Circuit.farmCerealManaged = new CircuitFarmLogic("managedCereal", new FarmLogicCereal());
		Circuit.farmVegetableManaged = new CircuitFarmLogic("managedVegetable", new FarmLogicVegetable());
		Circuit.farmInfernalManaged = new CircuitFarmLogic("managedInfernal", new FarmLogicInfernal());

		Circuit.farmPeatManual = new CircuitFarmLogic("manualPeat", new FarmLogicPeat()).setManual();
		Circuit.farmShroomManual = new CircuitFarmLogic("manualShroom", new FarmLogicMushroom()).setManual();
		Circuit.farmCerealManual = new CircuitFarmLogic("manualCereal", new FarmLogicCereal()).setManual();
		Circuit.farmVegetableManual = new CircuitFarmLogic("manualVegetable", new FarmLogicVegetable()).setManual();
		Circuit.farmSucculentManual = new CircuitFarmLogic("manualSucculent", new FarmLogicSucculent()).setManual();
		Circuit.farmPoalesManual = new CircuitFarmLogic("manualPoales", new FarmLogicReeds()).setManual();
		Circuit.farmGourdManual = new CircuitFarmLogic("manualGourd", new FarmLogicGourd()).setManual();
		Circuit.farmCocoaManual = new CircuitFarmLogic("manualCocoa", new FarmLogicCocoa()).setManual();

		Circuit.farmOrchardManual = new CircuitFarmLogic("manualOrchard", new FarmLogicOrchard());
	}

	@Override
	public void registerRecipes() {

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

			RecipeUtil.addRecipe(basic,
					"I#I",
					"WCW",
					'#', block.getBase(),
					'W', "slabWood",
					'C', PluginCore.items.tubes.get(EnumElectronTube.TIN, 1),
					'I', "ingotCopper");

			RecipeUtil.addRecipe(gearbox,
					" # ",
					"TTT",
					'#', basic,
					'T', "gearTin");

			RecipeUtil.addRecipe(hatch,
					" # ",
					"TDT",
					'#', basic,
					'T', "gearTin",
					'D', OreDictUtil.TRAPDOOR_WOOD);

			RecipeUtil.addRecipe(valve,
					" # ",
					"XTX",
					'#', basic,
					'T', "gearTin",
					'X', "blockGlass");

			RecipeUtil.addRecipe(control,
					" # ",
					"XTX",
					'#', basic,
					'T', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1),
					'X', "dustRedstone");
		}

		// Circuits
		ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
		ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");

		ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.COPPER, 1), Circuit.farmArborealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.TIN, 1), Circuit.farmPeatManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1), Circuit.farmCerealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.IRON, 1), Circuit.farmVegetableManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.BLAZE, 1), Circuit.farmInfernalManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, PluginCore.items.tubes.get(EnumElectronTube.APATITE, 1), Circuit.farmShroomManaged);

		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.COPPER, 1), Circuit.farmOrchardManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.TIN, 1), Circuit.farmPeatManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1), Circuit.farmCerealManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.IRON, 1), Circuit.farmVegetableManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1), Circuit.farmSucculentManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.DIAMOND, 1), Circuit.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.OBSIDIAN, 1), Circuit.farmGourdManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.APATITE, 1), Circuit.farmShroomManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, PluginCore.items.tubes.get(EnumElectronTube.LAPIS, 1), Circuit.farmCocoaManual);
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// mushrooms are a workaround for the farm and should not be obtainable
		hiddenItems.add(new ItemStack(blocks.mushroom, 1, OreDictionary.WILDCARD_VALUE));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		EnumFarmBlockType.registerSprites();
	}
}
