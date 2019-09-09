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
//package forestry.apiculture.commands;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//
//import forestry.api.apiculture.BeeManager;
//import forestry.api.apiculture.genetics.EnumBeeType;
//import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
//import forestry.api.apiculture.genetics.IBee;
//import forestry.api.apiculture.genetics.IBeeGenome;
//import forestry.api.genetics.AlleleManager;
//import forestry.api.genetics.IAllele;
//import forestry.core.commands.CommandHelpers;
//import forestry.core.commands.SpeciesNotFoundException;
//import forestry.core.commands.SubCommand;
//
//public class CommandBeeGive extends SubCommand {
//
//	private final String beeTypeHelpString;
//	private final String[] beeTypeArr;
//
//	public CommandBeeGive() {
//		super("give");
//		setPermLevel(PermLevel.ADMIN);
//
//		List<String> beeTypeStrings = new ArrayList<>();
//		for (EnumBeeType type : EnumBeeType.values()) {
//			beeTypeStrings.add(type.getName());
//		}
//
//		beeTypeArr = beeTypeStrings.toArray(new String[0]);
//
//		StringBuilder beeTypeHelp = new StringBuilder();
//		String separator = ", ";
//
//		Iterator<String> iter = beeTypeStrings.iterator();
//		while (iter.hasNext()) {
//			beeTypeHelp.append(iter.next());
//			if (iter.hasNext()) {
//				beeTypeHelp.append(separator);
//			}
//		}
//		beeTypeHelpString = beeTypeHelp.toString();
//	}
//
//	@Override
//	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//		if (args.length < 2) {
//			printHelp(sender);
//			return;
//		}
//
//		IBeeGenome beeGenome = getBeeGenome(args[0]);
//		EnumBeeType beeType = getBeeType(args[1]);
//		if (beeType == null) {
//			printHelp(sender);
//			return;
//		}
//
//		PlayerEntity player;
//		if (args.length == 3) {
//			player = CommandBase.getPlayer(server, sender, args[2]);
//		} else {
//			player = CommandBase.getPlayer(server, sender, sender.getName());
//		}
//
//		IBee bee = BeeManager.beeRoot.getBee(beeGenome);
//
//		if (beeType == EnumBeeType.QUEEN) {
//			bee.mate(bee);
//		}
//
//		ItemStack beeStack = BeeManager.beeRoot.getMemberStack(bee, beeType);
//		player.dropItem(beeStack, false, true);
//
//		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.bee.give.given", player.getName(), bee.getGenome().getPrimary().getAlleleName(), beeType.getName());
//	}
//
//	private static IBeeGenome getBeeGenome(String speciesName) throws SpeciesNotFoundException {
//		IAlleleBeeSpecies species = null;
//
//		for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {
//
//			if (!uid.equals(speciesName)) {
//				continue;
//			}
//
//			IAllele allele = AlleleManager.alleleRegistry.getAllele(uid);
//			if (allele instanceof IAlleleBeeSpecies) {
//				species = (IAlleleBeeSpecies) allele;
//				break;
//			}
//		}
//
//		if (species == null) {
//			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
//				if (allele instanceof IAlleleBeeSpecies && allele.getAlleleName().equals(speciesName)) {
//					species = (IAlleleBeeSpecies) allele;
//					break;
//				}
//			}
//		}
//
//		if (species == null) {
//			throw new SpeciesNotFoundException(speciesName);
//		}
//
//		IAllele[] template = BeeManager.beeRoot.getTemplate(species);
//
//		return BeeManager.beeRoot.templateAsGenome(template);
//	}
//
//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//		if (args.length == 1) {
//			List<String> tabCompletion = CommandHelpers.getListOfStringsMatchingLastWord(args, getSpecies());
//			tabCompletion.add("help");
//			return tabCompletion;
//		} else if (args.length == 2) {
//			return CommandHelpers.getListOfStringsMatchingLastWord(args, beeTypeArr);
//		} else if (args.length == 3) {
//			return CommandHelpers.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
//		}
//		return Collections.emptyList();
//	}
//
//	private static String[] getSpecies() {
//		List<String> species = new ArrayList<>();
//
//		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
//			if (allele instanceof IAlleleBeeSpecies) {
//				species.add(allele.getAlleleName());
//			}
//		}
//
//		return species.toArray(new String[0]);
//	}
//
//	@Nullable
//	private static EnumBeeType getBeeType(String beeTypeName) {
//		for (EnumBeeType beeType : EnumBeeType.values()) {
//			if (beeType.getName().equalsIgnoreCase(beeTypeName)) {
//				return beeType;
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public void printHelp(ICommandSender sender) {
//		super.printHelp(sender);
//		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.give.available", beeTypeHelpString);
//	}
//}
