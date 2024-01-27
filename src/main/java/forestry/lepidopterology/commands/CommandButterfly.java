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
package forestry.lepidopterology.commands;


import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import forestry.lepidopterology.features.LepidopterologyEntities;

import genetics.commands.PermLevel;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandButterfly {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("butterfly")
				.then(CommandButterflyKill.register());
	}

	public static class CommandButterflyKill {
        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("kill").requires(PermLevel.ADMIN).executes(CommandButterflyKill::execute);
        }

		public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			int killCount = 0;
			for (Entity butterfly : context.getSource().getPlayerOrException().getLevel().getEntities(LepidopterologyEntities.BUTTERFLY.entityType(), EntitySelector.ENTITY_STILL_ALIVE)) {
				butterfly.remove(Entity.RemovalReason.KILLED);
				killCount++;
			}
			context.getSource().sendSuccess(Component.translatable("for.chat.command.forestry.butterfly.kill.response", killCount), true);

			return killCount;
		}
	}
}
