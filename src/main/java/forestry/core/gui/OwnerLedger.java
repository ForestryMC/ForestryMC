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
package forestry.core.gui;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import forestry.core.interfaces.IAccessHandler;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.StringUtil;

/**
 * Ledger displaying ownership information
 */
public class OwnerLedger extends Ledger {

	private final IAccessHandler accessHandler;

	public OwnerLedger(LedgerManager manager, IRestrictedAccessTile tile) {
		super(manager, "owner");

		this.accessHandler = tile.getAccessHandler();

		Minecraft minecraft = Proxies.common.getClientInstance();
		boolean playerIsOwner = accessHandler.isOwner(minecraft.thePlayer);

		if (playerIsOwner) {
			maxHeight = 60;
		} else {
			maxHeight = 36;
		}
	}

	public boolean isAccessButton(int mouseX, int mouseY) {

		int shiftX = currentShiftX;
		int shiftY = currentShiftY + 44;

		return mouseX >= shiftX && mouseX <= currentShiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + 12;
	}

	@Override
	public boolean isVisible() {
		return accessHandler.isOwned();
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		EnumAccess accessType = accessHandler.getAccessType();
		IIcon accessIcon = TextureManager.getInstance().getDefault("misc/access." + accessType.toString().toLowerCase(Locale.ENGLISH));
		drawIcon(accessIcon, x + 3, y + 4);

		// Draw description
		if (!isFullyOpened()) {
			return;
		}

		drawHeader(StringUtil.localize("gui.owner"), x + 22, y + 8);

		drawText(PlayerUtil.getOwnerName(accessHandler), x + 22, y + 20);

		Minecraft minecraft = Proxies.common.getClientInstance();
		boolean playerIsOwner = accessHandler.isOwner(minecraft.thePlayer);
		if (playerIsOwner) {
			drawSubheader(StringUtil.localize("gui.access") + ':', x + 22, y + 32);
			// Access rules
			drawIcon(accessIcon, x + 20, y + 40);
			drawText(StringUtil.localize(accessType.getName()), x + 38, y + 44);
		}
	}

	@Override
	public String getTooltip() {
		return StringUtil.localize("gui.owner") + ": " + PlayerUtil.getOwnerName(accessHandler);
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (isAccessButton(x, y)) {
			Minecraft minecraft = Proxies.common.getClientInstance();
			EntityPlayer player = minecraft.thePlayer;

			return accessHandler.switchAccessRule(player);
		}

		return false;
	}
}
