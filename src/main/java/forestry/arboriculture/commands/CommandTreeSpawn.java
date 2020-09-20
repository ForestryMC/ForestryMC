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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.commands.PermLevel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.command.EnumArgument;

public final class CommandTreeSpawn {
    public static ArgumentBuilder<CommandSource, ?> register(String name, ITreeSpawner treeSpawner) {
        return Commands.literal(name).requires(PermLevel.ADMIN)
                .then(Commands.argument("type", EnumArgument.enumArgument(TreeDefinition.class))
                        .executes(a -> run(treeSpawner, a.getSource(), a.getArgument("type", TreeDefinition.class))))
                .executes(a -> run(treeSpawner, a.getSource(), TreeDefinition.Oak));
    }

    public static int run(
            ITreeSpawner treeSpawner,
            CommandSource source,
            TreeDefinition tree
    ) throws CommandSyntaxException {
        return treeSpawner.spawn(source, tree.createIndividual(), source.asPlayer());
    }
}
