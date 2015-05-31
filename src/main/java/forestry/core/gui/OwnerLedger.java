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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import forestry.core.interfaces.IOwnable;
import forestry.core.interfaces.IRestrictedAccess;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.StringUtil;

/**
 * Ledger displaying ownership information
 */
public class OwnerLedger extends Ledger {

	private final IOwnable tile;

	public OwnerLedger(LedgerManager manager, IOwnable tile) {
		super(manager, "owner");
		this.tile = tile;

		boolean playerIsOwner = tile.isOwner(manager.minecraft.thePlayer);

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
		return tile.isOwned();
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		EnumAccess access;
		if (tile instanceof IRestrictedAccess) {
			access = ((IRestrictedAccess) tile).getAccess();
		} else {
			access = EnumAccess.SHARED;
		}
		IIcon accessIcon = TextureManager.getInstance().getDefault("misc/access." + access.toString().toLowerCase(Locale.ENGLISH));
		drawIcon(accessIcon, x + 3, y + 4);

		// Draw description
		if (!isFullyOpened()) {
			return;
		}

		drawHeader(StringUtil.localize("gui.owner"), x + 22, y + 8);

		drawText(PlayerUtil.getOwnerName(tile), x + 22, y + 20);

		boolean playerIsOwner = tile.isOwner(manager.minecraft.thePlayer);
		if (playerIsOwner && tile instanceof IRestrictedAccess) {
			drawSubheader(StringUtil.localize("gui.access") + ':', x + 22, y + 32);
			// Access rules
			drawIcon(accessIcon, x + 20, y + 40);
			drawText(StringUtil.localize(access.getName()), x + 38, y + 44);
		}
	}

	@Override
	public String getTooltip() {
		return StringUtil.localize("gui.owner") + ": " + PlayerUtil.getOwnerName(tile);
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (isAccessButton(x, y) && tile instanceof IRestrictedAccess) {
			if (!Proxies.common.isSimulating(((TileEntity) tile).getWorldObj())) {
				TileEntity te = (TileEntity) tile;
				Proxies.net.sendToServer(new PacketCoordinates(PacketId.ACCESS_SWITCH, te));
			}

			((IRestrictedAccess) tile).switchAccessRule(manager.minecraft.thePlayer);
			return true;
		}

		return false;
	}
}
