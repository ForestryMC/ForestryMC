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
package forestry.core.gadgets;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.interfaces.IHintSource;

public abstract class TileBase extends TileForestry implements IHintSource {

	/* INTERACTION */
	public abstract void openGui(EntityPlayer player, TileBase tile);

	public boolean canDrainWithBucket() {
		return false;
	}

	/* IHINTSOURCE */
	private String[] hints;

	public void setHints(String[] hints) {
		this.hints = hints;
	}

	@Override
	public boolean hasHints() {
		return hints != null && hints.length > 0;
	}

	@Override
	public String[] getHints() {
		return hints;
	}
}
