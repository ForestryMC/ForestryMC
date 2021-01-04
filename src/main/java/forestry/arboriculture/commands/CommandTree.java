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
//import net.minecraft.command.CommandSource;
//
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//
//import forestry.core.commands.CommandMode;
//import forestry.core.commands.CommandSaveStats;
//import forestry.core.commands.ICommandModeHelper;
//import forestry.core.commands.IStatsSaveHelper;
//import forestry.core.commands.SubCommand;
//
//public class CommandTree {
//
//	public CommandTree(CommandDispatcher<CommandSource> dispatcher) {
//		super("tree");
//
//		IStatsSaveHelper saveHelper = new TreeStatsSaveHelper();
//		ICommandModeHelper modeHelper = new TreeModeHelper();
//
//		addChildCommand(new CommandTreeSpawn("spawnTree", new TreeSpawner()));
//		addChildCommand(new CommandTreeSpawn("spawnForest", new ForestSpawner()));
//		addChildCommand(new CommandMode(modeHelper));
//		addChildCommand(new CommandSaveStats(saveHelper, modeHelper));
//		dispatcher.register(
//			LiteralArgumentBuilder.literal("tree")
//			.then()
//		);
//	}
//
//}
