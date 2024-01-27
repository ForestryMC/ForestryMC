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
package genetics.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandHelpers {

	public static void sendLocalizedChatMessage(CommandSourceStack sender, String locTag, Object... args) {
		sender.sendSuccess(Component.translatable(locTag, args), false);
	}

	public static void sendLocalizedChatMessage(CommandSourceStack sender, Style chatStyle, String locTag, Object... args) {
		sender.sendSuccess(Component.translatable(locTag, args).setStyle(chatStyle), false);
	}

	/**
	 * Avoid using this function if at all possible. Commands are processed on the server,
	 * which has no localization information.
	 * <p>
	 * StringUtil.localize() is NOT a valid alternative for sendLocalizedChatMessage().
	 * Messages will not be localized properly if you use StringUtil.localize().
	 */
	public static void sendChatMessage(CommandSourceStack sender, String message) {
		sender.sendSuccess(Component.literal(message), false);
	}

}
