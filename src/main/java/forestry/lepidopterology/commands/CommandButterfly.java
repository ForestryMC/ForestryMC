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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forestry.core.commands.PermLevel;
import forestry.lepidopterology.features.LepidopterologyEntities;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandButterfly {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("butterfly")
                .then(CommandButterflyKill.register());
    }

    public static class CommandButterflyKill {
        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("kill").requires(PermLevel.ADMIN).executes(CommandButterflyKill::execute);
        }

        public static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            int killCount = 0;
            for (Entity butterfly : context.getSource()
                    .asPlayer()
                    .getServerWorld()
                    .getEntities(LepidopterologyEntities.BUTTERFLY.entityType(), EntityPredicates.IS_ALIVE)) {
                butterfly.remove();
                killCount++;
            }

            context.getSource()
                    .sendFeedback(new TranslationTextComponent(
                            "for.chat.command.forestry.butterfly.kill.response",
                            killCount
                    ), true);

            return killCount;
        }
    }

}
