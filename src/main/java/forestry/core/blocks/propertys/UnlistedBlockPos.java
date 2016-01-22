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

import net.minecraft.util.BlockPos;
import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedBlockPos implements IUnlistedProperty<BlockPos> {
	public static UnlistedBlockPos POS = new UnlistedBlockPos();

	@Override
	public String getName() {
		return "pos";
	}

	@Override
	public boolean isValid(final BlockPos value) {
		return true;
	}

	@Override
	public Class<BlockPos> getType() {
		return BlockPos.class;
	}

	@Override
	public String valueToString(final BlockPos value) {
		return null;
	}
}