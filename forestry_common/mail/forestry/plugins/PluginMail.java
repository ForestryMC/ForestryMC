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
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.api.core.PluginInfo;
import forestry.api.mail.EnumPostage;
import forestry.api.mail.PostManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.Trigger;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.mail.CommandMail;
import forestry.mail.EnumAddressee;
import forestry.mail.GuiHandlerMail;
import forestry.mail.PacketHandlerMail;
import forestry.mail.PostRegistry;
import forestry.mail.PostalCarrier;
import forestry.mail.SaveEventHandlerMail;
import forestry.mail.TickHandlerMailClient;
import forestry.mail.gadgets.MachineMailbox;
import forestry.mail.gadgets.MachinePhilatelist;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.items.ItemCatalogue;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemStamps;
import forestry.mail.items.ItemStamps.StampInfo;
import forestry.mail.proxy.ProxyMail;
import forestry.mail.triggers.TriggerBuffer;
import forestry.mail.triggers.TriggerHasMail;
import forestry.mail.triggers.TriggerLowInput;
import forestry.mail.triggers.TriggerLowPaper;
import forestry.mail.triggers.TriggerLowStamps;

@PluginInfo(pluginID = "Mail", name = "Mail", author = "SirSengir", url = Defaults.URL, description = "Adds Forestry's mail and trade system.")
public class PluginMail extends NativePlugin {

	@SidedProxy(clientSide = "forestry.mail.proxy.ClientProxyMail", serverSide = "forestry.mail.proxy.ProxyMail")
	public static ProxyMail proxy;
	public static Trigger triggerHasMail;
	public static Trigger lowPaper25;
	public static Trigger lowPaper10;
	public static Trigger lowPostage40;
	public static Trigger lowPostage20;
	public static Trigger lowInput25;
	public static Trigger lowInput10;
	public static Trigger highBuffer75;
	public static Trigger highBuffer90;
	public static MachineDefinition definitionMailbox;
	public static MachineDefinition definitionTradestation;
	public static MachineDefinition definitionPhilatelist;
	public static StampInfo[] stampDefinitions;

	@Override
	public boolean isAvailable() {
		return !Config.disableMail;
	}

	@Override
	public void preInit() {
		super.preInit();

		new TickHandlerMailClient();

		triggerHasMail = new TriggerHasMail();
		lowPaper25 = new TriggerLowPaper("mail.lowPaper.25", 0.25f);
		lowPaper10 = new TriggerLowPaper("mail.lowPaper.10", 0.1f);
		lowPostage40 = new TriggerLowStamps("mail.lowStamps.40", 40);
		lowPostage20 = new TriggerLowStamps("mail.lowStamps.20", 20);
		lowInput25 = new TriggerLowInput("mail.lowInput.25", 0.25f);
		lowInput10 = new TriggerLowInput("mail.lowInput.10", 0.1f);
		highBuffer75 = new TriggerBuffer("mail.lowBuffer.75", 0.75f);
		highBuffer90 = new TriggerBuffer("mail.lowBuffer.90", 0.90f);

		ForestryBlock.mail = new BlockBase(Material.iron);
		ForestryBlock.mail.setBlockName("for.mail");
		Proxies.common.registerBlock(ForestryBlock.mail, ItemForestryBlock.class);

		ShapedRecipeCustom recipe = ShapedRecipeCustom.createShapedRecipe(new ItemStack(ForestryBlock.mail, 1, Defaults.DEFINITION_MAILBOX_META),
				" # ", "#Y#", "XXX",
				'#', "ingotTin",
				'X', Blocks.chest,
				'Y', ForestryItem.sturdyCasing);
		definitionMailbox = ForestryBlock.mail.addDefinition(new MachineDefinition(Defaults.DEFINITION_MAILBOX_META, "forestry.Mailbox", MachineMailbox.class, recipe).setFaces(0, 1, 2, 2, 2, 2, 0, 7));


		recipe = ShapedRecipeCustom.createShapedRecipe(new ItemStack(ForestryBlock.mail, 1, Defaults.DEFINITION_TRADESTATION_META),
				"Z#Z", "#Y#", "XWX",
				'#', ForestryItem.tubes.getItemStack(1, 2),
				'X', Blocks.chest,
				'Y', ForestryItem.sturdyCasing,
				'Z', ForestryItem.tubes.getItemStack(1, 3),
				'W', ForestryItem.circuitboards.getItemStack(1, 2));
		definitionTradestation = ForestryBlock.mail.addDefinition(new MachineDefinition(Defaults.DEFINITION_TRADESTATION_META, "forestry.Tradestation", MachineTrader.class, recipe).setFaces(0, 1, 2, 3, 4, 4, 0, 7));

		definitionPhilatelist = ForestryBlock.mail.addDefinition(new MachineDefinition(Defaults.DEFINITION_PHILATELIST_META, "forestry.Philatelist", MachinePhilatelist.class)
		.setFaces(0, 1, 2, 3, 2, 2, 0, 7));



		PostManager.postRegistry = new PostRegistry();
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
	}

	@Override
	public void doInit() {
		super.doInit();

		proxy.addLocalizations();

		definitionMailbox.register();
		definitionTradestation.register();
		definitionPhilatelist.register();
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerMail();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerMail();
	}

	@Override
	protected void registerItems() {

		stampDefinitions = new StampInfo[] {
				new StampInfo("1n", EnumPostage.P_1, ForestryItem.apatite, 0x4a8ca7, 0xffffff), new StampInfo("2n", EnumPostage.P_2, "ingotCopper", 0xe8c814, 0xffffff),
				new StampInfo("5n", EnumPostage.P_5, "ingotTin", 0x9c0707, 0xffffff), new StampInfo("10n", EnumPostage.P_10, Items.gold_ingot, 0x7bd1b8, 0xffffff),
				new StampInfo("20n", EnumPostage.P_20, Items.diamond, 0xff9031, 0xfff7dd), new StampInfo("50n", EnumPostage.P_50, Items.emerald, 0x6431d7, 0xfff7dd),
				new StampInfo("100n", EnumPostage.P_100, Items.nether_star, 0xd731ba, 0xfff7dd) }; //new StampInfo("200n", EnumPostage.P_200, Item.netherStar, 0xcd9831, 0xfff7dd)};

		/* STAMPS */
		ForestryItem.stamps.registerItem(new ItemStamps(stampDefinitions), "stamps");

		/* LETTER */
		ForestryItem.letters.registerItem(new ItemLetter(), "letters");

		/* CATALOGUE */
		ForestryItem.catalogue.registerItem(new ItemCatalogue(), "catalogue");
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {
		// Letters
		Proxies.common.addShapelessRecipe(ForestryItem.letters.getItemStack(), Items.paper, ForestryItem.propolis.getItemStack(1, Defaults.WILDCARD));

		if (Config.craftingStampsEnabled) {
			for (int i = 0; i < stampDefinitions.length; i++) {
				if (Config.collectorStamps.contains(stampDefinitions[i].name))
					continue;

				Proxies.common.addRecipe(ForestryItem.stamps.getItemStack(9, i), new Object[] { "XXX", "###", "ZZZ", 'X',
					stampDefinitions[i].getCraftingIngredient(), '#', Items.paper, 'Z', ForestryItem.honeyDrop });
				RecipeManagers.carpenterManager.addRecipe(10, LiquidHelper.getLiquid(Defaults.LIQUID_SEEDOIL, 300), null, ForestryItem.stamps.getItemStack(9, i),
						new Object[] { "XXX", "###", 'X', stampDefinitions[i].getCraftingIngredient(), '#', Items.paper });
			}
		}

		// Recycling
		Proxies.common.addRecipe(new ItemStack(Items.paper), new Object[] { "###", '#', ForestryItem.letters.getItemStack(1, Defaults.WILDCARD) });

		// Carpenter
		RecipeManagers.carpenterManager.addRecipe(10, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 250), null, ForestryItem.letters.getItemStack(), new Object[] { "###", "###", '#', ForestryItem.woodPulp });

		Proxies.common.addShapelessRecipe(ForestryItem.catalogue.getItemStack(), new Object[] { ForestryItem.stamps.getItemStack(1, Defaults.WILDCARD), new ItemStack(Items.book) });
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[] { new CommandMail() };
	}
}
