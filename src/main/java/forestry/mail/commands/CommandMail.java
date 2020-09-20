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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.PermLevel;
import forestry.core.utils.StringUtil;
import forestry.mail.MailAddress;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandMail {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("mail")
                .then(CommandMailTrades.register())
                .then(CommandMailVirtualize.register());
    }

    public static class CommandMailTrades {
        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("trades").executes(CommandMailTrades::execute);
        }

        public static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            ServerPlayerEntity player = context.getSource().asPlayer();
            ServerWorld world = (ServerWorld) player.world;
            for (ITradeStation trade : PostManager.postRegistry.getPostOffice(world)
                    .getActiveTradeStations(world)
                    .values()) {
                CommandHelpers.sendChatMessage(context.getSource(), makeTradeListEntry(trade.getTradeInfo()));
            }

            return 1;
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

    public static class CommandMailVirtualize {
        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("virtualize").requires(PermLevel.ADMIN).executes(CommandMailVirtualize::execute);
        }

        public static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            ServerPlayerEntity player = context.getSource().asPlayer();

            World world = player.getEntityWorld();

            MailAddress address = new MailAddress(player.getGameProfile());
            ITradeStation trade = PostManager.postRegistry.getTradeStation((ServerWorld) world, address);

            if (trade == null) {
                Style red = Style.EMPTY;
                red.setFormatting(TextFormatting.RED);
                CommandHelpers.sendLocalizedChatMessage(
                        context.getSource(),
                        red,
                        "for.chat.command.forestry.mail.virtualize.no_tradestation",
                        player.getDisplayName()
                );
                return 0;
            }

            trade.setVirtual(!trade.isVirtual());
            Style green = Style.EMPTY;
            green.setFormatting(TextFormatting.GREEN);
            CommandHelpers.sendLocalizedChatMessage(
                    context.getSource(),
                    green,
                    "for.chat.command.forestry.mail.virtualize.set",
                    trade.getAddress().getName(),
                    trade.isVirtual()
            );

            return 1;
        }
    }
}
