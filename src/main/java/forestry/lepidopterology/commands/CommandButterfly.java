///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.lepidopterology.commands;
//
//import net.minecraft.command.ICommandSender;
//import net.minecraft.command.WrongUsageException;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.EntityPredicates;
//import net.minecraft.util.text.TranslationTextComponent;
//
//import forestry.core.commands.CommandHelpers;
//import forestry.core.commands.SubCommand;
//import forestry.lepidopterology.entities.EntityButterfly;
//
///**
// * @author CovertJaguar <http://www.railcraft.info/>
// */
//public class CommandButterfly extends SubCommand {
////TODO commands
//	public CommandButterfly() {
//		super("butterfly");
//		addAlias("bfly");
//		addChildCommand(new CommandButterflyKill());
//	}
//
//	public static class CommandButterflyKill extends SubCommand {
//
//		public CommandButterflyKill() {
//			super("kill");
//			addAlias("killall");
//			setPermLevel(PermLevel.ADMIN);
//		}
//
//		@Override
//		public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws WrongUsageException {
//			if (args.length > 1) {
//				CommandHelpers.throwWrongUsage(sender, this);
//			}
//			int killCount = 0;
//			for (EntityButterfly butterfly : sender.getEntityWorld().getEntities(EntityButterfly.class, EntityPredicates.IS_ALIVE)) {
//				butterfly.setDead();
//				killCount++;
//			}
//			sender.sendMessage(new TranslationTextComponent("for.chat.command.forestry.butterfly.kill.response", killCount));
//		}
//	}
//
//}
