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
package forestry.arboriculture;

import javax.annotation.Nonnull;
import java.util.Collection;

import forestry.api.arboriculture.EnumWoodType;

public interface IWoodTyped {
	@Nonnull
	String getBlockKind();

	boolean isFireproof();

	@Nonnull
	EnumWoodType getWoodType(int meta);

	@Nonnull
	Collection<EnumWoodType> getWoodTypes();
}
