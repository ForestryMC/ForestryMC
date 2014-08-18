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

import java.util.List;

import forestry.api.mail.MailAddress;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CommandMC;
import forestry.core.utils.StringUtil;

public class CommandMail extends CommandMC {

	@Override
	public int compareTo(Object arg0) {
		return this.getCommandName().compareTo(((ICommand) arg0).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "mail";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " help";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length <= 0)
			throw new WrongUsageException(StringUtil.localizeAndFormat("chat.help", this.getCommandUsage(sender)));

		if (arguments[0].matches("trades")) {
			commandTrades(sender, arguments);
			return;
		} else if (arguments[0].matches("virtualize")) {
			commandVirtualize(sender, arguments);
			return;
		} else if (arguments[0].matches("help")) {
			sendChatMessage(sender, StringUtil.localizeAndFormat("chat.mail.command.help.0", this.getCommandName()));
			sendChatMessage(sender, StringUtil.localize("chat.mail.command.help.1"));
			sendChatMessage(sender, StringUtil.localize("chat.mail.command.help.2"));
			sendChatMessage(sender, StringUtil.localize("chat.mail.command.help.3"));
			return;
		}

		throw new WrongUsageException(this.getCommandUsage(sender));
	}

	private void commandTrades(ICommandSender sender, String[] arguments) {
		if (!(sender instanceof EntityPlayer))
			return;
		for (ITradeStation trade : PostManager.postRegistry.getPostOffice(((EntityPlayer) sender).worldObj).getActiveTradeStations(((EntityPlayer) sender).worldObj).values())
			sendChatMessage(sender, makeTradeListEntry(trade.getTradeInfo()));
	}

	private String makeTradeListEntry(TradeStationInfo info) {
		String entry = "\u00A7c";
		if (info.state == EnumStationState.OK)
			entry = "\u00A7a";

		String tradegood = "[ ? ]";
		if (info.tradegood != null)
			tradegood = info.tradegood.stackSize + "x" + info.tradegood.getDisplayName();
		String demand = "[ ? ]";
		if (info.required.length > 0) {
			demand = "";
			for (ItemStack dmd : info.required)
				demand = StringUtil.append(", ", demand, dmd.stackSize + "x" + dmd.getDisplayName());
		}

		return String.format("%s%-12s | %-20s | %s", entry, info.address.getIdentifierName(), tradegood, demand);
	}

	private void commandVirtualize(ICommandSender sender, String[] arguments) {
		if ((sender instanceof EntityPlayer && !Proxies.common.isOp((EntityPlayer) sender)) || (!sender.canCommandSenderUseCommand(4, getCommandName()))) {
			sendChatMessage(sender, "\u00a7c" + StringUtil.localize("chat.command.noperms"));
			return;
		}

		if (arguments.length <= 1)
			throw new WrongUsageException("/" + getCommandName() + " virtualize <tradestation-name>");

		World world = getWorld(sender, arguments);
		MailAddress address = new MailAddress(arguments[1]);
		ITradeStation trade = PostManager.postRegistry.getTradeStation(world, address);
		if (trade == null) {
			sendChatMessage(sender, String.format("\u00a7c" + StringUtil.localize("chat.mail.command.no_tradestation"), arguments[1]));
			return;
		}

		trade.setVirtual(!trade.isVirtual());
		sendChatMessage(sender, String.format("\u00A7aSet virtualization for '%s' to %s.", trade.getAddress().getIdentifierName(), trade.isVirtual()));
	}
}
