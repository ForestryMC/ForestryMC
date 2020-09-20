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
package forestry.arboriculture.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.core.commands.CommandMode;
import forestry.core.commands.CommandSaveStats;
import forestry.core.commands.ICommandModeHelper;
import forestry.core.commands.IStatsSaveHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandTree {
    public static ArgumentBuilder<CommandSource, ?> register() {
        IStatsSaveHelper saveHelper = new TreeStatsSaveHelper();
        ICommandModeHelper modeHelper = new TreeModeHelper();

        return Commands.literal("tree")
                .then(CommandTreeSpawn.register("spawnTree", new TreeSpawner()))
                .then(CommandTreeSpawn.register("spawnForest", new ForestSpawner()))
                .then(CommandMode.register(modeHelper))
                .then(CommandSaveStats.register(saveHelper, modeHelper));
    }
}
