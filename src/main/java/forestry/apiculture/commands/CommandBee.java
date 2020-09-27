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
package forestry.apiculture.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import forestry.core.commands.CommandMode;
import forestry.core.commands.CommandSaveStats;
import forestry.core.commands.ICommandModeHelper;
import forestry.core.commands.IStatsSaveHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandBee {
    public static ArgumentBuilder<CommandSource, ?> register() {
        IStatsSaveHelper saveHelper = new BeeStatsSaveHelper();
        ICommandModeHelper modeHelper = new BeeModeHelper();

        return Commands.literal("bee")
                       .then(CommandMode.register(modeHelper))
                       .then(CommandSaveStats.register(saveHelper, modeHelper))
                       .then(CommandBeeGive.register());
    }
}
