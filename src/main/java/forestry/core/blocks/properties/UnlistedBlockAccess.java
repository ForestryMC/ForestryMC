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
package forestry.core.blocks.properties;

import net.minecraft.world.IBlockReader;

import net.minecraftforge.client.model.data.ModelProperty;

public final class UnlistedBlockAccess extends ModelProperty<IBlockReader> {
	public static final UnlistedBlockAccess BLOCKACCESS = new UnlistedBlockAccess();

	public UnlistedBlockAccess() {
		super();
	}
	//	@Override
	//	public String getName() {
	//		return "blockaccess";
	//	}
	//
	//	@Override
	//	public boolean isValid(IBlockReader value) {
	//		return true;
	//	}
	//
	//	@Override
	//	public Class<IBlockReader> getType() {
	//		return IBlockReader.class;
	//	}
	//
	//	@Override
	//	public String valueToString(IBlockReader value) {
	//		return value.toString();
	//	}
}