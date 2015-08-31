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

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import forestry.core.config.Version;
import forestry.core.proxy.Proxies;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandVersion extends SubCommand {

	public CommandVersion() {
		super("version");
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		ChatStyle style = new ChatStyle();
		if (Version.isOutdated()) {
			style.setColor(EnumChatFormatting.RED);
		} else {
			style.setColor(EnumChatFormatting.GREEN);
		}

		CommandHelpers.sendLocalizedChatMessage(sender, style, "for.chat.version", Version.getVersion(), Proxies.common.getMinecraftVersion(), Version.getRecommendedVersion());
		if (Version.isOutdated()) {
			for (String updateLine : Version.getChangelog()) {
				CommandHelpers.sendChatMessage(sender, EnumChatFormatting.BLUE + updateLine);
			}
		}
	}

}
