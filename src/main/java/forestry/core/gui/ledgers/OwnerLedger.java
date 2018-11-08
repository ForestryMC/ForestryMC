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
package forestry.core.gui.ledgers;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.mojang.authlib.GameProfile;

import forestry.core.owner.IOwnedTile;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Translator;

/**
 * Ledger displaying ownership information
 */
public class OwnerLedger extends Ledger {
	@Nullable
	private final GameProfile owner;

	public OwnerLedger(LedgerManager manager, IOwnedTile tile) {
		super(manager, "owner");

		this.owner = tile.getOwnerHandler().getOwner();
		this.maxHeight = 40;
	}

	@Override
	public boolean isVisible() {
		return owner != null;
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		TextureAtlasSprite accessIcon = TextureManagerForestry.getInstance().getDefault("misc/access.shared");
		drawSprite(accessIcon, x + 3, y + 4);

		// Draw description
		if (isFullyOpened()) {
			drawHeader(Translator.translateToLocal("for.gui.owner"), x + 22, y + 8);
			drawText(PlayerUtil.getOwnerName(owner), x + 22, y + 20);
		}
	}

	@Override
	public String getTooltip() {
		return Translator.translateToLocal("for.gui.owner") + ": " + PlayerUtil.getOwnerName(owner);
	}
}
