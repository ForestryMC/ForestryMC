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

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.PostManager;
import forestry.api.modules.ForestryModule;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.mail.blocks.BlockRegistryMail;
import forestry.mail.gui.GuiCatalogue;
import forestry.mail.gui.GuiLetter;
import forestry.mail.gui.GuiMailbox;
import forestry.mail.gui.GuiStampCollector;
import forestry.mail.gui.GuiTradeName;
import forestry.mail.gui.GuiTrader;
import forestry.mail.gui.MailContainerTypes;
import forestry.mail.items.ItemRegistryMail;
import forestry.mail.network.PacketRegistryMail;
import forestry.mail.tiles.TileRegistryMail;
import forestry.mail.triggers.MailTriggers;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.MAIL, name = "Mail", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.mail.description")
public class ModuleMail extends BlankForestryModule {
	@Nullable
	private static ItemRegistryMail items;
	@Nullable
	private static BlockRegistryMail blocks;
	@Nullable
	private static MailContainerTypes containerTypes;
	@Nullable
	private static TileRegistryMail tiles;

	public static ItemRegistryMail getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	public static BlockRegistryMail getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static MailContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	public static TileRegistryMail getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	@Override
	public void setupAPI() {
		PostManager.postRegistry = new PostRegistry();
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
		PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
	}

	@Override
	public void registerItems() {
		items = new ItemRegistryMail();
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryMail();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryMail();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new MailContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		MailContainerTypes containers = getContainerTypes();
		ScreenManager.registerFactory(containers.CATALOGUE, GuiCatalogue::new);
		ScreenManager.registerFactory(containers.LETTER, GuiLetter::new);
		ScreenManager.registerFactory(containers.MAILBOX, GuiMailbox::new);
		ScreenManager.registerFactory(containers.STAMP_COLLECTOR, GuiStampCollector::new);
		ScreenManager.registerFactory(containers.TRADE_NAME, GuiTradeName::new);
		ScreenManager.registerFactory(containers.TRADER, GuiTrader::new);
	}

	@Override
	public void preInit() {
		//TODO commands
		//		ModuleCore.rootCommand.addChildCommand(new CommandMail());

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

		// Carpenter
		//		RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(FluidRegistry.WATER, 250), ItemStack.EMPTY, items.letters.getItemStack(), "###", "###", '#', coreItems.woodPulp);
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}
}
