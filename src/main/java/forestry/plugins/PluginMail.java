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
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.PostManager;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.GuiHandlerBase;
import forestry.core.ISaveEventHandler;
import forestry.core.blocks.BlockBase;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemBlockForestry;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.MachineDefinition;
import forestry.mail.GuiHandlerMail;
import forestry.mail.PostRegistry;
import forestry.mail.PostalCarrier;
import forestry.mail.SaveEventHandlerMail;
import forestry.mail.TickHandlerMailClient;
import forestry.mail.commands.CommandMail;
import forestry.mail.items.EnumStampDefinition;
import forestry.mail.items.ItemRegistryMail;
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

	public static ItemRegistryMail items;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		PostManager.postRegistry = new PostRegistry();
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
	}

	@Override
	protected void registerItemsAndBlocks() {
		items = new ItemRegistryMail();

		ForestryBlock.mail.registerBlock(new BlockBase(Material.iron), ItemBlockForestry.class, "mail");
	}

	@Override
	public void preInit() {
		super.preInit();
		
		PluginCore.rootCommand.addChildCommand(new CommandMail());

		if (Config.mailAlertEnabled) {
			FMLCommonHandler.instance().bus().register(new TickHandlerMailClient());
		}

		BlockBase mail = ((BlockBase) ForestryBlock.mail.block());

		definitionMailbox = new MachineDefinition(Constants.DEFINITION_MAILBOX_META, "forestry.Mailbox", TileMailbox.class)
				.setFaces(0, 1, 2, 2, 2, 2, 0, 7);
		mail.addDefinition(definitionMailbox);

		definitionTradestation = new MachineDefinition(Constants.DEFINITION_TRADESTATION_META, "forestry.Tradestation", TileTrader.class)
				.setFaces(0, 1, 2, 3, 4, 4, 0, 7);
		mail.addDefinition(definitionTradestation);

		definitionPhilatelist = new MachineDefinition(Constants.DEFINITION_PHILATELIST_META, "forestry.Philatelist", TilePhilatelist.class)
				.setFaces(0, 1, 2, 3, 2, 2, 0, 7);
		mail.addDefinition(definitionPhilatelist);
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
		Object stampGlue;
		Object letterGlue;

		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems != null) {
			stampGlue = beeItems.honeyDrop;
			letterGlue = beeItems.propolis.getWildcard();
		} else {
			stampGlue = Items.slime_ball;
			letterGlue = Items.slime_ball;
		}

		RecipeUtil.addShapelessRecipe(items.letters.getItemStack(), Items.paper, letterGlue);

		if (Config.craftingStampsEnabled) {
			for (EnumStampDefinition stampDefinition : EnumStampDefinition.VALUES) {
				if (Config.collectorStamps.contains(stampDefinition.getName())) {
					continue;
				}

				RecipeUtil.addRecipe(items.stamps.get(stampDefinition, 9),
						"XXX", "###", "ZZZ",
						'X', stampDefinition.getCraftingIngredient(),
						'#', Items.paper,
						'Z', stampGlue);
				RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEEDOIL.getFluid(300), null, items.stamps.get(stampDefinition, 9),
						"XXX", "###",
						'X', stampDefinition.getCraftingIngredient(),
						'#', Items.paper);
			}
		}

		// Recycling
		RecipeUtil.addRecipe(new ItemStack(Items.paper), "###", '#', new ItemStack(items.letters, 1, OreDictionary.WILDCARD_VALUE));

		// Carpenter
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(250), null, items.letters.getItemStack(), "###", "###", '#', PluginCore.items.woodPulp);

		RecipeUtil.addShapelessRecipe(items.catalogue.getItemStack(), new ItemStack(items.stamps, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.book));

		RecipeUtil.addRecipe(ForestryBlock.mail.getItemStack(1, Constants.DEFINITION_MAILBOX_META),
				" # ",
				"#Y#",
				"XXX",
				'#', "ingotTin",
				'X', "chestWood",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(ForestryBlock.mail.getItemStack(1, Constants.DEFINITION_TRADESTATION_META),
				"Z#Z",
				"#Y#",
				"XWX",
				'#', PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1),
				'X', "chestWood",
				'Y', PluginCore.items.sturdyCasing,
				'Z', PluginCore.items.tubes.get(EnumElectronTube.IRON, 1),
				'W', PluginCore.items.circuitboards.getCircuitboard(EnumCircuitBoardType.REFINED));
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}
}
