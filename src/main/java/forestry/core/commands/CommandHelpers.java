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
package forestry.core.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandHelpers {
    public static void sendLocalizedChatMessage(CommandSource sender, String locTag, Object... args) {
        sender.sendFeedback(new TranslationTextComponent(locTag, args), false);
    }

    public static void sendLocalizedChatMessage(CommandSource sender, Style chatStyle, String locTag, Object... args) {
        TranslationTextComponent chat = new TranslationTextComponent(locTag, args);
        chat.setStyle(chatStyle);
        sender.sendFeedback(chat, false);
    }

    /**
     * Avoid using this function if at all possible. Commands are processed on the server,
     * which has no localization information.
     * <p>
     * StringUtil.localize() is NOT a valid alternative for sendLocalizedChatMessage().
     * Messages will not be localized properly if you use StringUtil.localize().
     */
    public static void sendChatMessage(CommandSource sender, String message) {
        sender.sendFeedback(new StringTextComponent(message), false);
    }
}
