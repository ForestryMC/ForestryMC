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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.PluginInfo;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmable;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.Trigger;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.farming.EventHandlerFarming;
import forestry.farming.FarmHelper;
import forestry.farming.GuiHandlerFarming;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.gadgets.BlockFarm;
import forestry.farming.gadgets.BlockMushroom;
import forestry.farming.gadgets.TileControl;
import forestry.farming.gadgets.TileFarm.EnumFarmBlock;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.farming.gadgets.TileGearbox;
import forestry.farming.gadgets.TileHatch;
import forestry.farming.gadgets.TileValve;
import forestry.farming.items.ItemFarmBlock;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.logic.FarmLogicCereal;
import forestry.farming.logic.FarmLogicCocoa;
import forestry.farming.logic.FarmLogicGourd;
import forestry.farming.logic.FarmLogicInfernal;
import forestry.farming.logic.FarmLogicOrchard;
import forestry.farming.logic.FarmLogicPeat;
import forestry.farming.logic.FarmLogicPoale;
import forestry.farming.logic.FarmLogicRubber;
import forestry.farming.logic.FarmLogicShroom;
import forestry.farming.logic.FarmLogicSucculent;
import forestry.farming.logic.FarmLogicVegetable;
import forestry.farming.logic.FarmableGE;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;
import forestry.farming.logic.FarmableGourd;
import forestry.farming.logic.FarmableStacked;
import forestry.farming.logic.FarmableVanillaSapling;
import forestry.farming.logic.FarmableVanillaShroom;
import forestry.farming.proxy.ProxyFarming;
import forestry.farming.triggers.TriggerLowFertilizer;
import forestry.farming.triggers.TriggerLowLiquid;
import forestry.farming.triggers.TriggerLowSoil;

@PluginInfo(pluginID = "Farming", name = "Farming", author = "SirSengir", url = Defaults.URL, description = "Adds automatic farms and harvesters for a wide variety of products.")
public class PluginFarming extends NativePlugin {

	@SidedProxy(clientSide = "forestry.farming.proxy.ClientProxyFarming", serverSide = "forestry.farming.proxy.ProxyFarming")
	public static ProxyFarming proxy;
	public static int modelIdFarmBlock;
	public static ItemStack farmFertilizer;
	public static Trigger lowResourceLiquid50;
	public static Trigger lowResourceLiquid25;
	public static Trigger lowSoil128;
	public static Trigger lowSoil64;
	public static Trigger lowSoil32;
	public static Trigger lowFertilizer50;
	public static Trigger lowFertilizer25;

	@Override
	public boolean isAvailable() {
		return !Config.disableFarming;
	}

	@Override
	public void preInit() {
		super.preInit();

		Farmables.farmInterface = new FarmHelper();

		ForestryBlock.mushroom = new BlockMushroom();

		Farmables.farmables.put("farmArboreal", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmArboreal").add(new FarmableVanillaSapling());
		Farmables.farmables.get("farmArboreal").add(new FarmableGE());

		Farmables.farmables.put("farmShroom", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmShroom").add(new FarmableVanillaShroom(Blocks.brown_mushroom, 0));
		Farmables.farmables.get("farmShroom").add(new FarmableVanillaShroom(Blocks.red_mushroom, 0));

		Farmables.farmables.put("farmWheat", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(new ItemStack(Items.wheat_seeds), Blocks.wheat, 7));

		Farmables.farmables.put("farmGourd", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmGourd").add(
				new FarmableGourd(new ItemStack(Items.pumpkin_seeds), new ItemStack(Blocks.pumpkin_stem), new ItemStack(Blocks.pumpkin)));
		Farmables.farmables.get("farmGourd").add(new FarmableGourd(new ItemStack(Items.melon_seeds), new ItemStack(Blocks.melon_stem), new ItemStack(Blocks.melon_block)));

		Farmables.farmables.put("farmInfernal", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmInfernal").add(new FarmableGenericCrop(new ItemStack(Items.nether_wart), Blocks.nether_wart, 3));

		Farmables.farmables.put("farmPoales", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmPoales").add(new FarmableStacked(Blocks.reeds, 3));

		Farmables.farmables.put("farmSucculentes", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmSucculentes").add(new FarmableStacked(Blocks.cactus, 3));

		Farmables.farmables.put("farmVegetables", new ArrayList<IFarmable>());
		Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(Items.potato), Blocks.potatoes, 7));
		Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(Items.carrot), Blocks.carrots, 7));

		ForestryBlock.farm = new BlockFarm().setBlockName("ffarm");
		Proxies.common.registerBlock(ForestryBlock.farm, ItemFarmBlock.class);
		/*Item.itemsList[ForestryBlock.farm] = null;
		Item.itemsList[ForestryBlock.farm] = (new ItemFarmBlock(ForestryBlock.farm - 256, "ffarm"));*/
		ForestryBlock.farm.setHarvestLevel("pickaxe", 0);

		proxy.initializeRendering();

		// Triggers
		lowResourceLiquid50 = new TriggerLowLiquid("lowLiquid.50", 0.5f);
		lowResourceLiquid25 = new TriggerLowLiquid("lowLiquid.25", 0.25f);
		lowSoil128 = new TriggerLowSoil(128);
		lowSoil64 = new TriggerLowSoil(64);
		lowSoil32 = new TriggerLowSoil(32);
		lowFertilizer50 = new TriggerLowFertilizer("lowFertilizer.50", 0.5f);
		lowFertilizer25 = new TriggerLowFertilizer("lowFertilizer.25", 0.25f);

		// Layouts
		ICircuitLayout layoutManaged = new CircuitLayout("farms.managed");
		ChipsetManager.circuitRegistry.registerLayout(layoutManaged);
		ICircuitLayout layoutManual = new CircuitLayout("farms.manual");
		ChipsetManager.circuitRegistry.registerLayout(layoutManual);
	}

	@Override
	public void doInit() {
		super.doInit();

		proxy.addLocalizations();

		GameRegistry.registerTileEntity(TileFarmPlain.class, "forestry.Farm");
		GameRegistry.registerTileEntity(TileGearbox.class, "forestry.FarmGearbox");
		GameRegistry.registerTileEntity(TileHatch.class, "forestry.FarmHatch");
		GameRegistry.registerTileEntity(TileValve.class, "forestry.FarmValve");
		GameRegistry.registerTileEntity(TileControl.class, "forestry.FarmControl");

		Circuit.farmArborealManaged = new CircuitFarmLogic("managedArboreal", FarmLogicArboreal.class, new String[] { "Arboreal Plants" });
		Circuit.farmShroomManaged = new CircuitFarmLogic("managedShroom", FarmLogicShroom.class, new String[] { "Mushrooms" });
		Circuit.farmPeatManaged = new CircuitFarmLogic("managedPeat", FarmLogicPeat.class, new String[] { "Peat Bog" });
		Circuit.farmCerealManaged = new CircuitFarmLogic("managedCereal", FarmLogicCereal.class, new String[] { "Crops" });
		Circuit.farmVegetableManaged = new CircuitFarmLogic("managedVegetable", FarmLogicVegetable.class, new String[] { "Vegetables" });
		Circuit.farmInfernalManaged = new CircuitFarmLogic("managedInfernal", FarmLogicInfernal.class, new String[] { "Infernal Crops" });

		Circuit.farmPeatManual = new CircuitFarmLogic("manualPeat", FarmLogicPeat.class, new String[] { "Peat Bog" }).setManual();
		Circuit.farmShroomManual = new CircuitFarmLogic("manualShroom", FarmLogicShroom.class, new String[] { "Mushrooms" }).setManual();
		Circuit.farmCerealManual = new CircuitFarmLogic("manualCereal", FarmLogicCereal.class, new String[] { "Crops" }).setManual();
		Circuit.farmVegetableManual = new CircuitFarmLogic("manualVegetable", FarmLogicVegetable.class, new String[] { "Vegetables" }).setManual();
		Circuit.farmSucculentManual = new CircuitFarmLogic("manualSucculent", FarmLogicSucculent.class, new String[] { "Succulents" }).setManual();
		Circuit.farmPoalesManual = new CircuitFarmLogic("manualPoales", FarmLogicPoale.class, new String[] { "Reeds" }).setManual();
		Circuit.farmGourdManual = new CircuitFarmLogic("manualGourd", FarmLogicGourd.class, new String[] { "Gourds" }).setManual();
		Circuit.farmCocoaManual = new CircuitFarmLogic("manualCocoa", FarmLogicCocoa.class, new String[] { "Cocoa Plantation" }).setManual();

		Circuit.farmOrchardManual = new CircuitFarmLogic("manualOrchard", FarmLogicOrchard.class, new String[] { "Orchard" });
		Circuit.farmRubberManual = new CircuitFarmLogic("manualRubber", FarmLogicRubber.class, new String[] { "Rubber Plantation" });

		MinecraftForge.EVENT_BUS.register(new EventHandlerFarming());
	}

	@Override
	public void postInit() {
		super.postInit();
		farmFertilizer = ForestryItem.fertilizerCompound.getItemStack();
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {

		if (message.key.equals("add-farmable-sapling")) {
			String[] tokens = message.getStringValue().split("@");
			String errormsg = String.format("Received an invalid 'add-farmable-sapling' request '%s' from mod %s", message.getStringValue(),
					message.getSender());
			if (tokens.length != 2) {
				Proxies.log.info(errormsg + ".");
				return true;
			}

			if (!Farmables.farmables.containsKey(tokens[0])) {
				Proxies.log.info("%s for non-existent farm %s.", errormsg, tokens[0]);
				return true;
			}

			String[] items = tokens[1].split("[\\.]+");
			if (items.length != 2 && items.length != 4) {
				Proxies.log.info("%s for farm '%s': id definitions did not match.", errormsg, tokens[0]);
				return true;
			}

			try {
				Block sapling = GameData.getBlockRegistry().getRaw(items[0]);
				if (sapling == null || sapling == Blocks.air) throw new RuntimeException("can't find block for "+items[0]);

				if (items.length == 2) {
					Farmables.farmables.get(tokens[0]).add(new FarmableGenericSapling(sapling, Integer.parseInt(items[1])));
				} else {
					Item windfall = GameData.getItemRegistry().getRaw(items[2]);
					if (windfall == null) throw new RuntimeException("can't find item for "+items[2]);

					Farmables.farmables.get(tokens[0]).add(
							new FarmableGenericSapling(sapling, Integer.parseInt(items[1]),
									new ItemStack(windfall, 1, Integer.parseInt(items[3]))));
				}
			} catch (Exception ex) {
				Proxies.log.info("%s for farm '%s': ", errormsg, tokens[0], ex.getMessage());
			}
			return true;

		} else if (message.key.equals("add-farmable-crop")) {

			String[] tokens = message.getStringValue().split("@");
			String errormsg = String.format("Received an invalid 'add-farmable-crop' request '%s' from mod %s", message.getStringValue(), message.getSender());
			if (tokens.length != 2) {
				Proxies.log.info(errormsg + ".");
				return true;
			}

			if (!Farmables.farmables.containsKey(tokens[0])) {
				Proxies.log.info("%s for non-existent farm %s.", errormsg, tokens[0]);
				return true;
			}

			String[] items = tokens[1].split("[\\.]+");
			if (items.length != 4) {
				Proxies.log.info("%s for farm '%s': id definitions did not match.", errormsg, tokens[0]);
				return true;
			}

			try {
				Item seed = GameData.getItemRegistry().getRaw(items[0]);
				if (seed == null) throw new RuntimeException("can't find item for "+items[0]);
				Block crop = GameData.getBlockRegistry().getRaw(items[2]);
				if (crop == null || crop == Blocks.air) throw new RuntimeException("can't find block for "+items[2]);

				Farmables.farmables.get(tokens[0]).add(
						new FarmableGenericCrop(new ItemStack(seed, 1, Integer.parseInt(items[1])),
								crop,
								Integer.parseInt(items[3])));
			} catch (Exception ex) {
				Proxies.log.info("%s for farm '%s': ", errormsg, tokens[0], ex.getMessage());
			}

			return true;
		}

		return false;
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerFarming();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	protected void registerItems() {
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {

		ItemStack basic = new ItemStack(ForestryBlock.farm, 1, 0);
		for (EnumFarmBlock block : EnumFarmBlock.values()) {
			NBTTagCompound compound = new NBTTagCompound();
			block.saveToCompound(compound);

			basic.setTagCompound((NBTTagCompound) compound.copy());
			ShapedRecipeCustom.buildRecipe(basic.copy(), "I#I", "WCW", '#', block.getCraftingIngredient(), 'W', "slabWood", 'C', ForestryItem.tubes.getItemStack(1, 1), 'I', "ingotCopper");

		}

		ItemStack gearbox = new ItemStack(ForestryBlock.farm, 1, 2);
		ShapedRecipeCustom.buildRecipe(gearbox, " # ", "TTT", '#', basic, 'T', "gearTin").setPreserveNBT();

		ItemStack hatch = new ItemStack(ForestryBlock.farm, 1, 3);
		ShapedRecipeCustom.buildRecipe(hatch, " # ", "TDT", '#', basic, 'T', "gearTin", 'D', Blocks.trapdoor).setPreserveNBT();

		ItemStack valve = new ItemStack(ForestryBlock.farm, 1, 4);
		ShapedRecipeCustom.buildRecipe(valve, " # ", "XTX", '#', basic, 'T', "gearTin", 'X', Blocks.glass).setPreserveNBT();

		ItemStack control = new ItemStack(ForestryBlock.farm, 1, 5);
		ShapedRecipeCustom.buildRecipe(control, " # ", "XTX", '#', basic, 'T', ForestryItem.tubes.getItemStack(1, 4), 'X', Items.redstone).setPreserveNBT();

		// Circuits
		ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
		ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");

		ChipsetManager.solderManager.addRecipe(layoutManaged, ForestryItem.tubes.getItemStack(1, 0), Circuit.farmArborealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, ForestryItem.tubes.getItemStack(1, 1), Circuit.farmPeatManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, ForestryItem.tubes.getItemStack(1, 2), Circuit.farmCerealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, ForestryItem.tubes.getItemStack(1, 3), Circuit.farmVegetableManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, ForestryItem.tubes.getItemStack(1, 7), Circuit.farmInfernalManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, ForestryItem.tubes.getItemStack(1, 10), Circuit.farmShroomManaged);

		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 0), Circuit.farmOrchardManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 1), Circuit.farmPeatManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 2), Circuit.farmCerealManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 3), Circuit.farmVegetableManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 4), Circuit.farmSucculentManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 5), Circuit.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 6), Circuit.farmGourdManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 10), Circuit.farmShroomManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 11), Circuit.farmCocoaManual);

		if (PluginIC2.resin != null && PluginIC2.rubberwood != null)
			ChipsetManager.solderManager.addRecipe(layoutManual, ForestryItem.tubes.getItemStack(1, 8), Circuit.farmRubberManual);
	}
}
