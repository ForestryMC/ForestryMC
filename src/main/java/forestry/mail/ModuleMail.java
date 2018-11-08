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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.circuits.ICircuit;
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.PostManager;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.mail.blocks.BlockRegistryMail;
import forestry.mail.commands.CommandMail;
import forestry.mail.items.EnumStampDefinition;
import forestry.mail.items.ItemRegistryMail;
import forestry.mail.network.PacketRegistryMail;
import forestry.mail.triggers.MailTriggers;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.MAIL, name = "Mail", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.mail.description")
public class ModuleMail extends BlankForestryModule {
	@Nullable
	private static ItemRegistryMail items;
	@Nullable
	private static BlockRegistryMail blocks;

	public static ItemRegistryMail getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	public static BlockRegistryMail getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

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
		ModuleCore.rootCommand.addChildCommand(new CommandMail());

		if (Config.mailAlertEnabled) {
			MinecraftForge.EVENT_BUS.register(new EventHandlerMailAlert());
		}
	}

	@Override
	public void registerTriggers() {
		MailTriggers.initialize();
	}

	@Override
	public void doInit() {
		BlockRegistryMail blocks = getBlocks();
		blocks.mailbox.init();
		blocks.tradeStation.init();
		blocks.stampCollector.init();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryMail();
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		ItemRegistryMail items = getItems();
		BlockRegistryMail blocks = getBlocks();

		ItemStack stampGlue;
		ItemStack letterGlue;

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
			stampGlue = beeItems.honeyDrop.getItemStack();
			letterGlue = beeItems.propolis.getWildcard();
		} else {
			stampGlue = new ItemStack(Items.SLIME_BALL);
			letterGlue = new ItemStack(Items.SLIME_BALL);
		}

		RecipeUtil.addShapelessRecipe("letter", items.letters.getItemStack(), Items.PAPER, letterGlue);

		if (Config.craftingStampsEnabled) {
			for (EnumStampDefinition stampDefinition : EnumStampDefinition.VALUES) {
				if (Config.collectorStamps.contains(stampDefinition.getUid())) {
					continue;
				}

				ItemStack stamps = items.stamps.get(stampDefinition, 9);

				RecipeUtil.addRecipe("stamps_" + stampDefinition.getUid(), stamps,
					"XXX",
					"###",
					"ZZZ",
					'X', stampDefinition.getCraftingIngredient(),
					'#', Items.PAPER,
					'Z', stampGlue);
				RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEED_OIL.getFluid(300), ItemStack.EMPTY, stamps,
					"XXX",
					"###",
					'X', stampDefinition.getCraftingIngredient(),
					'#', Items.PAPER);
			}
		}

		// Recycling
		RecipeUtil.addRecipe("letter_recycling", new ItemStack(Items.PAPER), "###", '#', OreDictUtil.EMPTIED_LETTER_ORE_DICT);

		// Carpenter
		RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(FluidRegistry.WATER, 250), ItemStack.EMPTY, items.letters.getItemStack(), "###", "###", '#', coreItems.woodPulp);

		RecipeUtil.addShapelessRecipe("catalogue", items.catalogue.getItemStack(), items.stamps.getWildcard(), new ItemStack(Items.BOOK));

		RecipeUtil.addRecipe("mailbox", new ItemStack(blocks.mailbox),
			" # ",
			"#Y#",
			"XXX",
			'#', "ingotTin",
			'X', "chestWood",
			'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("trade_station", new ItemStack(blocks.tradeStation),
			"Z#Z",
			"#Y#",
			"XWX",
			'#', coreItems.tubes.get(EnumElectronTube.BRONZE, 1),
			'X', "chestWood",
			'Y', coreItems.sturdyCasing,
			'Z', coreItems.tubes.get(EnumElectronTube.IRON, 1),
			'W', ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{}));
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}
}
