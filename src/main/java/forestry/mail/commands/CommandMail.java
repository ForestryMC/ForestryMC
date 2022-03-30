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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import forestry.api.mail.ITradeStation;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.utils.StringUtil;
import forestry.mail.MailAddress;

import genetics.commands.CommandHelpers;
import genetics.commands.PermLevel;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandMail {

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("mail")
				.then(CommandMailTrades.register())
				.then(CommandMailVirtualize.register());
	}

	public static class CommandMailTrades {

		public static ArgumentBuilder<CommandSourceStack, ?> register() {
			return Commands.literal("trades").executes(CommandMailTrades::execute);
		}

		public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();
			ServerLevel world = (ServerLevel) player.level;
			for (ITradeStation trade : PostManager.postRegistry.getPostOffice(world).getActiveTradeStations(world).values()) {
				CommandHelpers.sendChatMessage(context.getSource(), makeTradeListEntry(trade.getTradeInfo()));
			}

			return 1;
		}

		private static String makeTradeListEntry(ITradeStationInfo info) {
			ChatFormatting formatting = info.getState().isOk() ? ChatFormatting.GREEN : ChatFormatting.RED;

			String tradegood = "[ ? ]";
			if (!info.getTradegood().isEmpty()) {
				tradegood = info.getTradegood().getCount() + "x" + info.getTradegood().getHoverName();
			}
			String demand = "[ ? ]";
			if (!info.getRequired().isEmpty()) {
				demand = "";
				for (ItemStack dmd : info.getRequired()) {
					demand = StringUtil.append(", ", demand, dmd.getCount() + "x" + dmd.getHoverName());
				}
			}

			return String.format("%s%-12s | %-20s | %s", formatting, info.getAddress().getName(), tradegood, demand);
		}
	}

	public static class CommandMailVirtualize {
		public static ArgumentBuilder<CommandSourceStack, ?> register() {
		    return Commands.literal("virtualize").requires(PermLevel.ADMIN).executes(CommandMailVirtualize::execute);
        }

		public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			Level world = player.getCommandSenderWorld();

			MailAddress address = new MailAddress(player.getGameProfile());
			ITradeStation trade = PostManager.postRegistry.getTradeStation((ServerLevel) world, address);

			if (trade == null) {
				Style red = Style.EMPTY;
				red.withColor(ChatFormatting.RED);
				CommandHelpers.sendLocalizedChatMessage(context.getSource(), red, "for.chat.command.forestry.mail.virtualize.no_tradestation", player.getDisplayName());
				return 0;
			}

			trade.setVirtual(!trade.isVirtual());
			Style green = Style.EMPTY;
			green.withColor(ChatFormatting.GREEN);
			CommandHelpers.sendLocalizedChatMessage(context.getSource(), green, "for.chat.command.forestry.mail.virtualize.set", trade.getAddress().getName(), trade.isVirtual());

			return 1;
		}
	}
}
