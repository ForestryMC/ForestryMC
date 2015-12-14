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
package forestry.apiculture.genetics;

import forestry.api.apiculture.IBeeIconColourProvider;

public class DefaultBeeIconColourProvider implements IBeeIconColourProvider {
	private final int primaryColour;
	private final int secondaryColour;

	public DefaultBeeIconColourProvider(int primaryColour, int secondaryColour) {
		this.primaryColour = primaryColour;
		this.secondaryColour = secondaryColour;
	}

	@Override
	public int getIconColour(int renderPass) {
		if (renderPass == 0) {
			return primaryColour;
		}
		if (renderPass == 1) {
			return secondaryColour;
		}
		return 0xffffff;
	}
}
