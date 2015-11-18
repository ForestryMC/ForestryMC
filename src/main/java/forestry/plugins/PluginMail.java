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

import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.EnumPostage;
import forestry.api.mail.PostManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GuiHandlerBase;
import forestry.core.ISaveEventHandler;
import forestry.core.blocks.BlockBase;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemWithGui;
import forestry.core.network.GuiId;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.tiles.MachineDefinition;
import forestry.mail.GuiHandlerMail;
import forestry.mail.PostRegistry;
import forestry.mail.PostalCarrier;
import forestry.mail.SaveEventHandlerMail;
import forestry.mail.TickHandlerMailClient;
import forestry.mail.commands.CommandMail;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemStamps;
import forestry.mail.items.ItemStamps.StampInfo;
import forestry.mail.network.PacketRegistryMail;
import forestry.mail.tiles.TileMailbox;
import forestry.mail.tiles.TilePhilatelist;
import forestry.mail.tiles.TileTrader;
import forestry.mail.triggers.MailTriggers;

@Plugin(pluginID = "Mail", name = "Mail", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.mail.description")
public class PluginMail extends ForestryPlugin {

	private static MachineDefinition definitionMailbox;
	private static MachineDefinition definitionTradestation;
	private static MachineDefinition definitionPhilatelist;
	private static StampInfo[] stampDefinitions;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		PostManager.postRegistry = new PostRegistry();
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
	}

	@Override
	protected void registerItemsAndBlocks() {

		stampDefinitions = new StampInfo[]{
				new StampInfo("1n", EnumPostage.P_1, PluginCore.items.apatite, 0x4a8ca7, 0xffffff),
				new StampInfo("2n", EnumPostage.P_2, "ingotCopper", 0xe8c814, 0xffffff),
				new StampInfo("5n", EnumPostage.P_5, "ingotTin", 0x9c0707, 0xffffff),
				new StampInfo("10n", EnumPostage.P_10, Items.gold_ingot, 0x7bd1b8, 0xffffff),
				new StampInfo("20n", EnumPostage.P_20, Items.diamond, 0xff9031, 0xfff7dd),
				new StampInfo("50n", EnumPostage.P_50, Items.emerald, 0x6431d7, 0xfff7dd),
				new StampInfo("100n", EnumPostage.P_100, Items.nether_star, 0xd731ba, 0xfff7dd)}; //new StampInfo("200n", EnumPostage.P_200, Item.netherStar, 0xcd9831, 0xfff7dd)};

		/* STAMPS */
		ForestryItem.stamps.registerItem(new ItemStamps(stampDefinitions), "stamps");

		/* LETTER */
		ForestryItem.letters.registerItem(new ItemLetter(), "letters");

		/* CATALOGUE */
		Item itemCatalogue = new ItemWithGui(GuiId.CatalogueGUI).setMaxStackSize(1);
		ForestryItem.catalogue.registerItem(itemCatalogue, "catalogue");

		ForestryBlock.mail.registerBlock(new BlockBase(Material.iron), ItemBlockForestry.class, "mail");

	}

	@Override
	public void preInit() {
		super.preInit();
		
		PluginCore.rootCommand.addChildCommand(new CommandMail());

		if (Config.mailAlertEnabled) {
			FMLCommonHandler.instance().bus().register(new TickHandlerMailClient());
		}

		ShapedRecipeCustom recipe = ShapedRecipeCustom.createShapedRecipe(ForestryBlock.mail.getItemStack(1, Constants.DEFINITION_MAILBOX_META),
				" # ", "#Y#", "XXX",
				'#', "ingotTin",
				'X', "chestWood",
				'Y', PluginCore.items.sturdyCasing);

		BlockBase mail = ((BlockBase) ForestryBlock.mail.block());

		definitionMailbox = mail.addDefinition(new MachineDefinition(Constants.DEFINITION_MAILBOX_META, "forestry.Mailbox", TileMailbox.class, recipe).setFaces(0, 1, 2, 2, 2, 2, 0, 7));

		recipe = ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.mail.getItemStack(1, Constants.DEFINITION_TRADESTATION_META),
				"Z#Z",
				"#Y#",
				"XWX",
				'#', PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1),
				'X', "chestWood",
				'Y', PluginCore.items.sturdyCasing,
				'Z', PluginCore.items.tubes.get(EnumElectronTube.IRON, 1),
				'W', PluginCore.items.circuitboards.getCircuitboard(EnumCircuitBoardType.REFINED));
		definitionTradestation = mail.addDefinition(new MachineDefinition(Constants.DEFINITION_TRADESTATION_META, "forestry.Tradestation", TileTrader.class, recipe).setFaces(0, 1, 2, 3, 4, 4, 0, 7));

		definitionPhilatelist = mail.addDefinition(new MachineDefinition(Constants.DEFINITION_PHILATELIST_META, "forestry.Philatelist", TilePhilatelist.class)
				.setFaces(0, 1, 2, 3, 2, 2, 0, 7));
	}

	@Override
	protected void registerTriggers() {
		MailTriggers.initialize();
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionMailbox.register();
		definitionTradestation.register();
		definitionPhilatelist.register();
	}

	@Override
	public GuiHandlerBase getGuiHandler() {
		return new GuiHandlerMail();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryMail();
	}

	@Override
	protected void registerRecipes() {
		// Letters

		Item stampGlue;
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			RecipeUtil.addShapelessRecipe(ForestryItem.letters.getItemStack(), Items.paper, PluginApiculture.items.propolis.getWildcard());
			stampGlue = PluginApiculture.items.honeyDrop;
		} else {
			RecipeUtil.addShapelessRecipe(ForestryItem.letters.getItemStack(), Items.paper, Items.slime_ball);
			stampGlue = Items.slime_ball;
		}

		if (Config.craftingStampsEnabled) {
			for (int i = 0; i < stampDefinitions.length; i++) {
				if (Config.collectorStamps.contains(stampDefinitions[i].getName())) {
					continue;
				}

				RecipeUtil.addRecipe(ForestryItem.stamps.getItemStack(9, i),
						"XXX", "###", "ZZZ",
						'X', stampDefinitions[i].getCraftingIngredient(),
						'#', Items.paper,
						'Z', stampGlue);
				RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEEDOIL.getFluid(300), null, ForestryItem.stamps.getItemStack(9, i),
						"XXX", "###",
						'X', stampDefinitions[i].getCraftingIngredient(),
						'#', Items.paper);
			}
		}

		// Recycling
		RecipeUtil.addRecipe(new ItemStack(Items.paper), "###", '#', ForestryItem.letters.getItemStack(1, OreDictionary.WILDCARD_VALUE));

		// Carpenter
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(250), null, ForestryItem.letters.getItemStack(), "###", "###", '#', PluginCore.items.woodPulp);

		RecipeUtil.addShapelessRecipe(ForestryItem.catalogue.getItemStack(), ForestryItem.stamps.getItemStack(1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.book));
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}
}
