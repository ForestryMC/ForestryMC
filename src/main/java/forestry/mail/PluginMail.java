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
package forestry.mail;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.PostManager;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.ISaveEventHandler;
import forestry.core.PluginCore;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.mail.blocks.BlockRegistryMail;
import forestry.mail.blocks.BlockTypeMail;
import forestry.mail.commands.CommandMail;
import forestry.mail.items.EnumStampDefinition;
import forestry.mail.items.ItemRegistryMail;
import forestry.mail.network.PacketRegistryMail;
import forestry.mail.triggers.MailTriggers;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.MAIL, name = "Mail", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.mail.description")
public class PluginMail extends BlankForestryPlugin {
	public static ItemRegistryMail items;
	public static BlockRegistryMail blocks;

	@Override
	public void setupAPI() {
		PostManager.postRegistry = new PostRegistry();
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryMail();
		blocks = new BlockRegistryMail();
	}

	@Override
	public void preInit() {
		super.preInit();
		
		PluginCore.rootCommand.addChildCommand(new CommandMail());

		if (Config.mailAlertEnabled) {
			MinecraftForge.EVENT_BUS.register(new EventHandlerMailAlert());
		}

		blocks.mail.addDefinitions(BlockTypeMail.VALUES);
	}

	@Override
	public void registerTriggers() {
		MailTriggers.initialize();
	}

	@Override
	public void doInit() {
		super.doInit();

		blocks.mail.init();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryMail();
	}

	@Override
	public void registerRecipes() {
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

				ItemStack stamps = items.stamps.get(stampDefinition, 9);

				RecipeUtil.addRecipe(stamps,
						"XXX",
						"###",
						"ZZZ",
						'X', stampDefinition.getCraftingIngredient(),
						'#', Items.paper,
						'Z', stampGlue);
				RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEEDOIL.getFluid(300), null, stamps,
						"XXX",
						"###",
						'X', stampDefinition.getCraftingIngredient(),
						'#', Items.paper);
			}
		}

		// Recycling
		RecipeUtil.addRecipe(new ItemStack(Items.paper), "###", '#', ItemRegistryMail.emptiedLetterOreDict);

		// Carpenter
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(250), null, items.letters.getItemStack(), "###", "###", '#', PluginCore.items.woodPulp);

		RecipeUtil.addShapelessRecipe(items.catalogue.getItemStack(), items.stamps.getWildcard(), new ItemStack(Items.book));

		RecipeUtil.addRecipe(blocks.mail.get(BlockTypeMail.MAILBOX),
				" # ",
				"#Y#",
				"XXX",
				'#', "ingotTin",
				'X', "chestWood",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.mail.get(BlockTypeMail.TRADE_STATION),
				"Z#Z",
				"#Y#",
				"XWX",
				'#', PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1),
				'X', "chestWood",
				'Y', PluginCore.items.sturdyCasing,
				'Z', PluginCore.items.tubes.get(EnumElectronTube.IRON, 1),
				'W', PluginCore.items.circuitboards.get(EnumCircuitBoardType.REFINED));
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}
}
