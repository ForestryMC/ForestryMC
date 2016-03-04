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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
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
import forestry.api.farming.IFarmable;
import forestry.core.PluginCore;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.logic.FarmLogicCereal;
import forestry.farming.logic.FarmLogicCocoa;
import forestry.farming.logic.FarmLogicGourd;
import forestry.farming.logic.FarmLogicInfernal;
import forestry.farming.logic.FarmLogicOrchard;
import forestry.farming.logic.FarmLogicPeat;
import forestry.farming.logic.FarmLogicReeds;
import forestry.farming.logic.FarmLogicShroom;
import forestry.farming.logic.FarmLogicSucculent;
import forestry.farming.logic.FarmLogicVegetable;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGE;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;
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

		Farmables.farmables.put("farmArboreal", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmArboreal").add(new FarmableVanillaSapling());
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			Farmables.farmables.get("farmArboreal").add(new FarmableGE());
		}
		
		Farmables.farmables.put("farmOrchard", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(Blocks.wheat, 7));
		Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(Blocks.potatoes, 7));
		Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(Blocks.carrots, 7));

		Farmables.farmables.put("farmShroom", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmShroom").add(new FarmableVanillaMushroom(Blocks.brown_mushroom, 0));
		Farmables.farmables.get("farmShroom").add(new FarmableVanillaMushroom(Blocks.red_mushroom, 0));

		Farmables.farmables.put("farmWheat", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(new ItemStack(Items.wheat_seeds), Blocks.wheat, 7));

		Farmables.farmables.put("farmGourd", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmGourd").add(
				new FarmableGourd(new ItemStack(Items.pumpkin_seeds), new ItemStack(Blocks.pumpkin_stem), new ItemStack(Blocks.pumpkin)));
		Farmables.farmables.get("farmGourd").add(new FarmableGourd(new ItemStack(Items.melon_seeds), new ItemStack(Blocks.melon_stem), new ItemStack(Blocks.melon_block)));

		Farmables.farmables.put("farmInfernal", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmInfernal").add(new FarmableGenericCrop(new ItemStack(Items.nether_wart), Blocks.nether_wart, 3));

		Farmables.farmables.put("farmPoales", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmPoales").add(new FarmableStacked(Blocks.reeds, 3, 0));

		Farmables.farmables.put("farmSucculentes", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmSucculentes").add(new FarmableStacked(Blocks.cactus, 3, 0));

		Farmables.farmables.put("farmVegetables", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(Items.potato), Blocks.potatoes, 7));
		Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(Items.carrot), Blocks.carrots, 7));

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

		Circuit.farmArborealManaged = new CircuitFarmLogic("managedArboreal", FarmLogicArboreal.class);
		Circuit.farmShroomManaged = new CircuitFarmLogic("managedShroom", FarmLogicShroom.class);
		Circuit.farmPeatManaged = new CircuitFarmLogic("managedPeat", FarmLogicPeat.class);
		Circuit.farmCerealManaged = new CircuitFarmLogic("managedCereal", FarmLogicCereal.class);
		Circuit.farmVegetableManaged = new CircuitFarmLogic("managedVegetable", FarmLogicVegetable.class);
		Circuit.farmInfernalManaged = new CircuitFarmLogic("managedInfernal", FarmLogicInfernal.class);

		Circuit.farmPeatManual = new CircuitFarmLogic("manualPeat", FarmLogicPeat.class).setManual();
		Circuit.farmShroomManual = new CircuitFarmLogic("manualShroom", FarmLogicShroom.class).setManual();
		Circuit.farmCerealManual = new CircuitFarmLogic("manualCereal", FarmLogicCereal.class).setManual();
		Circuit.farmVegetableManual = new CircuitFarmLogic("manualVegetable", FarmLogicVegetable.class).setManual();
		Circuit.farmSucculentManual = new CircuitFarmLogic("manualSucculent", FarmLogicSucculent.class).setManual();
		Circuit.farmPoalesManual = new CircuitFarmLogic("manualPoales", FarmLogicReeds.class).setManual();
		Circuit.farmGourdManual = new CircuitFarmLogic("manualGourd", FarmLogicGourd.class).setManual();
		Circuit.farmCocoaManual = new CircuitFarmLogic("manualCocoa", FarmLogicCocoa.class).setManual();

		Circuit.farmOrchardManual = new CircuitFarmLogic("manualOrchard", FarmLogicOrchard.class);
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {

		if (message.key.equals("add-farmable-sapling")) {
			String[] tokens = message.getStringValue().split("@");
			String errormsg = IMCUtil.getInvalidIMCMessageText(message);
			if (tokens.length != 2) {
				Log.warning(errormsg);
				return true;
			}

			if (!Farmables.farmables.containsKey(tokens[0])) {
				Log.warning("%s For non-existent farm %s.", errormsg, tokens[0]);
				return true;
			}

			Collection<IFarmable> farmables = Farmables.farmables.get(tokens[0]);

			String itemString = tokens[1];

			Matcher matcher = Pattern.compile("(.+?)\\.(-?[0-9][0-9]?)(?:\\.(.+?)\\.(-?[0-9][0-9]?))?").matcher(itemString);
			if (!matcher.matches()) {
				Log.warning("%s For farm '%s': unable to parse string.", errormsg, tokens[0]);
				return true;
			}

			MatchResult matchResult = matcher.toMatchResult();
			String saplingString = matchResult.group(1);
			String saplingMetaString = matchResult.group(2);
			String windfallString = matchResult.group(3);
			String windfallMetaString = matchResult.group(4);

			try {
				Block sapling = ItemStackUtil.getBlockFromRegistry(saplingString);
				if (sapling == null || sapling == Blocks.air) {
					throw new IllegalArgumentException("can't find block for " + saplingString);
				}
				int saplingMeta = Integer.parseInt(saplingMetaString);

				if (windfallString == null) {
					farmables.add(new FarmableGenericSapling(sapling, saplingMeta));
				} else {
					Item windfall = ItemStackUtil.getItemFromRegistry(windfallString);
					if (windfall == null) {
						throw new IllegalArgumentException("can't find item for " + windfallString);
					}

					ItemStack windfallStack = new ItemStack(windfall, 1, Integer.parseInt(windfallMetaString));

					farmables.add(new FarmableGenericSapling(sapling, saplingMeta, windfallStack));
				}
			} catch (IllegalArgumentException e) {
				Log.warning("%s for farm '%s'", errormsg, tokens[0], e.getMessage());
			}
			return true;

		} else if (message.key.equals("add-farmable-crop")) {

			String[] tokens = message.getStringValue().split("@");
			String errormsg = IMCUtil.getInvalidIMCMessageText(message);
			if (tokens.length != 2) {
				Log.warning(errormsg);
				return true;
			}

			if (!Farmables.farmables.containsKey(tokens[0])) {
				Log.warning("%s For non-existent farm %s.", errormsg, tokens[0]);
				return true;
			}

			String[] items = tokens[1].split("[\\.]+");
			if (items.length != 4) {
				Log.warning("%s For farm '%s': id definitions did not match.", errormsg, tokens[0]);
				return true;
			}

			try {
				Item seed = ItemStackUtil.getItemFromRegistry(items[0]);
				if (seed == null) {
					throw new IllegalArgumentException("can't find item for " + items[0]);
				}
				Block crop = ItemStackUtil.getBlockFromRegistry(items[2]);
				if (crop == null || crop == Blocks.air) {
					throw new IllegalArgumentException("can't find block for " + items[2]);
				}

				Farmables.farmables.get(tokens[0]).add(
						new FarmableGenericCrop(new ItemStack(seed, 1, Integer.parseInt(items[1])),
								crop,
								Integer.parseInt(items[3])));
			} catch (IllegalArgumentException e) {
				Log.warning("%s for farm '%s': %s", errormsg, tokens[0], e.getMessage());
			}

			return true;
		}

		return false;
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
					'D', Blocks.trapdoor);

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
