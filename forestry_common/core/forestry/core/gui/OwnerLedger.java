/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

	IOwnable tile;

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

		String owner;
		if ((owner = tile.getOwnerName()) == null)
			owner = StringUtil.localize("gui.derelict");

		manager.minecraft.fontRenderer.drawString(owner, x + 22, y + 20, manager.gui.fontColor.get("ledger.owner.text"));

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
		if (tile.getOwnerName() != null)
			return StringUtil.localize("gui.owner") + ": " + tile.getOwnerName();
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
