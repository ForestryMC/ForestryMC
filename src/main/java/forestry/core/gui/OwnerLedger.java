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
import forestry.core.network.PacketIds;
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
		super(manager);
		this.tile = tile;
	}

	public boolean isAccessButton(int mouseX, int mouseY) {

		int shiftX = currentShiftX;
		int shiftY = currentShiftY + 44;

		return mouseX >= shiftX && mouseX <= currentShiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + 12;
	}

	@Override
	public void draw(int x, int y) {

		// Update state
		boolean playerIsOwner = tile.isOwner(manager.minecraft.thePlayer);

		if (playerIsOwner) {
			maxHeight = 60;
		} else {
			maxHeight = 36;
		}

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

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.owner"), x + 22, y + 8, manager.gui.fontColor.get("ledger.owner.header"));

		manager.minecraft.fontRenderer.drawString(PlayerUtil.getOwnerName(tile), x + 22, y + 20, manager.gui.fontColor.get("ledger.owner.text"));

		if (playerIsOwner && tile instanceof IRestrictedAccess) {
			manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.access") + ":", x + 22, y + 32,
					manager.gui.fontColor.get("ledger.owner.subheader"));
			// Access rules
			drawIcon(accessIcon, x + 20, y + 40);
			manager.minecraft.fontRenderer.drawString(StringUtil.localize(access.getName()), x + 38, y + 44, manager.gui.fontColor.get("ledger.owner.text"));
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
				Proxies.net.sendToServer(new PacketCoordinates(PacketIds.ACCESS_SWITCH, te.xCoord, te.yCoord, te.zCoord));
			}

			((IRestrictedAccess) tile).switchAccessRule(manager.minecraft.thePlayer);
			return true;
		}

		return false;
	}

}
