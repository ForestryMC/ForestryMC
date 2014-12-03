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
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.EnumAccess;
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

		if (mouseX >= shiftX && mouseX <= currentShiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + 12)
			return true;
		return false;
	}

	public boolean isOwnerChangeButton(int mouseX, int mouseY) {
		return false;
	}

	@Override
	public void draw(int x, int y) {

		// Update state
		boolean playerIsOwner = tile.isOwner(manager.minecraft.thePlayer);
		EnumAccess access = tile.getAccess();

		if (playerIsOwner)
			maxHeight = 60;
		else
			maxHeight = 36;

		// Draw background
		drawBackground(x, y);

		// Draw icon
		IIcon accessIcon = TextureManager.getInstance().getDefault("misc/access." + access.toString().toLowerCase(Locale.ENGLISH));
		drawIcon(accessIcon, x + 3, y + 4);

		// Draw description
		if (!isFullyOpened())
			return;

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.owner"), x + 22, y + 8, manager.gui.fontColor.get("ledger.owner.header"));

		String ownerName = StringUtil.localize("gui.derelict");
		
		if (tile.getOwnerProfile() != null)
			ownerName = tile.getOwnerProfile().getName();
		
		manager.minecraft.fontRenderer.drawString(ownerName, x + 22, y + 20, manager.gui.fontColor.get("ledger.owner.text"));

		if (!playerIsOwner)
			return;

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.access") + ":", x + 22, y + 32,
				manager.gui.fontColor.get("ledger.owner.subheader"));
		// Access rules
		drawIcon(accessIcon, x + 20, y + 40);
		manager.minecraft.fontRenderer.drawString(StringUtil.localize(access.getName()), x + 38, y + 44, manager.gui.fontColor.get("ledger.owner.text"));

	}

	@Override
	public String getTooltip() {
		if (tile.getOwnerProfile() != null)
			return StringUtil.localize("gui.owner") + ": " + tile.getOwnerProfile().getName();
		else
			return StringUtil.localize("gui.derelict");
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (isAccessButton(x, y)) {
			if (!Proxies.common.isSimulating(((TileEntity) tile).getWorldObj())) {
				TileEntity te = (TileEntity) tile;
				Proxies.net.sendToServer(new PacketCoordinates(PacketIds.ACCESS_SWITCH, te.xCoord, te.yCoord, te.zCoord));
			}

			tile.switchAccessRule(manager.minecraft.thePlayer);
			return true;
		}

		return false;
	}

}
