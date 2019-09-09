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
//package forestry.arboriculture.commands;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//
//import net.minecraft.command.CommandSource;
//import net.minecraft.command.Commands;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.command.arguments.serializers.StringArgumentSerializer;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//
//import com.mojang.brigadier.arguments.StringArgumentType;
//import com.mojang.brigadier.builder.ArgumentBuilder;
//
//import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
//import forestry.api.genetics.AlleleManager;
//import forestry.api.genetics.IAllele;
//import forestry.core.commands.CommandHelpers;
//import forestry.core.commands.SpeciesNotFoundException;
//import forestry.core.commands.TemplateNotFoundException;
//
//public final class CommandTreeSpawn {
//
//	static ArgumentBuilder<CommandSource, ?> register(String name, ITreeSpawner treeSpawner) {
//		return Commands.literal(name)
//			.requires(cs->cs.hasPermissionLevel(2))
//			.then(Commands.argument("tree_name", StringArgumentType.word()).executes(context -> {
//				PlayerEntity playerEntity = context.getSource().asPlayer();
//				String treeName = StringArgumentType.getString(context, "tree_name");
//				return treeSpawner.spawn(context.getSource(), treeName, playerEntity);
//				})
//			/*.then(Commands.literal("help").executes((context) -> {
//
//			})*/)
//	}
//
////TODO commands
//	private final ITreeSpawner treeSpawner;
//
//	public CommandTreeSpawn(String name, ITreeSpawner treeSpawner) {
//		super(name);
//		setPermLevel(PermLevel.ADMIN);
//		this.treeSpawner = treeSpawner;
//	}
//
//	@Override
//	public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws SpeciesNotFoundException, TemplateNotFoundException {
//		if (args.length < 1 || args.length > 2) {
//			printHelp(sender);
//			return;
//		}
//
//		if (sender instanceof PlayerEntity) {
//			PlayerEntity player = (PlayerEntity) sender;
//			String treeName = StringUtils.join(args, " ");
//
//			boolean success = treeSpawner.spawn(sender, treeName, player);
//			if (!success) {
//				printHelp(sender);
//			}
//		}
//	}
//
//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//		if (args.length == 1) {
//			List<String> tabCompletion = CommandHelpers.getListOfStringsMatchingLastWord(args, getSpecies());
//			tabCompletion.add("help");
//			return tabCompletion;
//		}
//		return Collections.emptyList();
//	}
//
//	private static String[] getSpecies() {
//		List<String> species = new ArrayList<>();
//
//		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
//			if (allele instanceof IAlleleTreeSpecies) {
//				species.add(allele.getAlleleName().replaceAll("\\s", ""));
//			}
//		}
//
//		return species.toArray(new String[0]);
//	}
//
//}
