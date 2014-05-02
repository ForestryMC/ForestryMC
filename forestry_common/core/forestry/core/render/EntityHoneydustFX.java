/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
