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

import net.minecraft.util.math.BlockPos;

import net.minecraftforge.client.model.data.ModelProperty;

//TODO - I thimmk this is right
public final class UnlistedBlockPos extends ModelProperty<BlockPos> {
	public static final UnlistedBlockPos POS = new UnlistedBlockPos();

	public UnlistedBlockPos() {
		super();
	}
}