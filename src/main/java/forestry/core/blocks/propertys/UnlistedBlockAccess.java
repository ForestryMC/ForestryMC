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
package forestry.core.blocks.propertys;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedBlockAccess implements IUnlistedProperty<IBlockAccess> {
	public static UnlistedBlockAccess BLOCKACCESS = new UnlistedBlockAccess();

	@Override
	public String getName() {
		return "ba";
	}

	@Override
	public boolean isValid(final IBlockAccess value) {
		return true;
	}

	@Override
	public Class<IBlockAccess> getType() {
		return IBlockAccess.class;
	}

	@Override
	public String valueToString(final IBlockAccess value) {
		return null;
	}
}