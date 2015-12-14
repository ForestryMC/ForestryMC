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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
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
		public void processSubCommand(ICommandSender sender, String[] args) {
			if (!(sender instanceof EntityPlayer)) {
				return;
			}
			for (ITradeStation trade : PostManager.postRegistry.getPostOffice(((EntityPlayer) sender).worldObj).getActiveTradeStations(((EntityPlayer) sender).worldObj).values()) {
				CommandHelpers.sendChatMessage(sender, makeTradeListEntry(trade.getTradeInfo()));
			}
		}

		private static String makeTradeListEntry(TradeStationInfo info) {
			EnumChatFormatting formatting = info.state.isOk() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;

			String tradegood = "[ ? ]";
			if (info.tradegood != null) {
				tradegood = info.tradegood.stackSize + "x" + info.tradegood.getDisplayName();
			}
			String demand = "[ ? ]";
			if (info.required.length > 0) {
				demand = "";
				for (ItemStack dmd : info.required) {
					demand = StringUtil.append(", ", demand, dmd.stackSize + "x" + dmd.getDisplayName());
				}
			}

			return String.format("%s%-12s | %-20s | %s", formatting, info.address.getName(), tradegood, demand);
		}
	}

	public static class CommandMailVirtualize extends SubCommand {

		public CommandMailVirtualize() {
			super("virtualize");
			addAlias("virt");
			setPermLevel(PermLevel.ADMIN);
		}

		@Override
		public void processSubCommand(ICommandSender sender, String[] args) {
			if (args.length != 1) {
				CommandHelpers.throwWrongUsage(sender, this);
			}

			World world = CommandHelpers.getWorld(sender, this);
			MailAddress address = new MailAddress(args[0]);
			ITradeStation trade = PostManager.postRegistry.getTradeStation(world, address);
			if (trade == null) {
				ChatStyle red = new ChatStyle();
				red.setColor(EnumChatFormatting.RED);
				CommandHelpers.sendLocalizedChatMessage(sender, red, "for.chat.command.forestry.mail.virtualize.no_tradestation", args[0]);
				return;
			}

			trade.setVirtual(!trade.isVirtual());
			ChatStyle green = new ChatStyle();
			green.setColor(EnumChatFormatting.GREEN);
			CommandHelpers.sendLocalizedChatMessage(sender, green, "for.chat.command.forestry.mail.virtualize.set", trade.getAddress().getName(), trade.isVirtual());
		}
	}
}
