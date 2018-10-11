/*
 *******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 *******************************************************************************
 */
package forestry.mail.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import forestry.api.mail.ITradeStation;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SubCommand;
import forestry.core.utils.StringUtil;
import forestry.mail.MailAddress;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandMail extends SubCommand {

	public CommandMail() {
		super("mail");
		addChildCommand(new CommandMailTrades());
		addChildCommand(new CommandMailVirtualize());
		addAlias("ml");
	}

	public static class CommandMailTrades extends SubCommand {

		public CommandMailTrades() {
			super("trades");
			addAlias("tr");
		}

		@Override
		public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) {
			if (!(sender instanceof EntityPlayer)) {
				return;
			}
			for (ITradeStation trade : PostManager.postRegistry.getPostOffice(((EntityPlayer) sender).world).getActiveTradeStations(((EntityPlayer) sender).world).values()) {
				CommandHelpers.sendChatMessage(sender, makeTradeListEntry(trade.getTradeInfo()));
			}
		}

		private static String makeTradeListEntry(ITradeStationInfo info) {
			TextFormatting formatting = info.getState().isOk() ? TextFormatting.GREEN : TextFormatting.RED;

			String tradegood = "[ ? ]";
			if (!info.getTradegood().isEmpty()) {
				tradegood = info.getTradegood().getCount() + "x" + info.getTradegood().getDisplayName();
			}
			String demand = "[ ? ]";
			if (!info.getRequired().isEmpty()) {
				demand = "";
				for (ItemStack dmd : info.getRequired()) {
					demand = StringUtil.append(", ", demand, dmd.getCount() + "x" + dmd.getDisplayName());
				}
			}

			return String.format("%s%-12s | %-20s | %s", formatting, info.getAddress().getName(), tradegood, demand);
		}
	}

	public static class CommandMailVirtualize extends SubCommand {

		public CommandMailVirtualize() {
			super("virtualize");
			addAlias("virt");
			setPermLevel(PermLevel.ADMIN);
		}

		@Override
		public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 1) {
				CommandHelpers.throwWrongUsage(sender, this);
			}

			World world = sender.getEntityWorld();
			MailAddress address = new MailAddress(args[0]);
			ITradeStation trade = PostManager.postRegistry.getTradeStation(world, address);
			if (trade == null) {
				Style red = new Style();
				red.setColor(TextFormatting.RED);
				CommandHelpers.sendLocalizedChatMessage(sender, red, "for.chat.command.forestry.mail.virtualize.no_tradestation", args[0]);
				return;
			}

			trade.setVirtual(!trade.isVirtual());
			Style green = new Style();
			green.setColor(TextFormatting.GREEN);
			CommandHelpers.sendLocalizedChatMessage(sender, green, "for.chat.command.forestry.mail.virtualize.set", trade.getAddress().getName(), trade.isVirtual());
		}
	}
}
