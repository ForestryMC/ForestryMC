/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.render;

import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.world.World;

public class EntityHoneydustFX extends EntityReddustFX {

	public EntityHoneydustFX(World world, double d, double d1, double d2, float f, float f1, float f2) {
		this(world, d, d1, d2, 1.0F, f, f1, f2);
	}

	public EntityHoneydustFX(World world, double d, double d1, double d2, float f, float f1, float f2, float f3) {
		super(world, d, d1, d2, f, f1, f2, f3);
		// float f4 = (float)Math.random() * 0.4F + 0.6F;
		// particleRed = ((float)(Math.random() * 0.20000000298023224D) + 0.95F)
		// * f1 * f4;
		// particleGreen = ((float)(Math.random() * 0.20000000298023224D) +
		// 0.75F) * f2 * f4;
		// particleBlue = ((float)(Math.random() * 0.20000000298023224D) + 0.1F)
		// * f3 * f4;
		particleRed = 0.9F;
		particleGreen = 0.75F;
		particleBlue = 0F;
	}

}
