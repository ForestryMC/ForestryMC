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
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandVersion extends SubCommand {

	@Override
	public String getCommandName() {
		return "version";
	}

	@Override
	public String getCommandFormat(ICommandSender sender) {
		return "/" + getFullCommandString();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		String colour = Version.isOutdated() ? "\u00A7c" : "\u00A7a";

		CommandHelpers.sendChatMessage(sender, String.format(colour + StringUtil.localize("chat.version"), Version.getVersion(), Proxies.common.getMinecraftVersion(), Version.getRecommendedVersion()));
		if (Version.isOutdated())
			for (String updateLine : Version.getChangelog()) {
				CommandHelpers.sendChatMessage(sender, "\u00A79" + updateLine);
			}
	}

}
