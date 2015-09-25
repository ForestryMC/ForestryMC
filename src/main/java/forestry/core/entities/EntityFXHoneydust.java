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
package forestry.core.entities;

import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.world.World;

public class EntityFXHoneydust extends EntityReddustFX {

	public EntityFXHoneydust(World world, double d, double d1, double d2, float f, float f1, float f2) {
		this(world, d, d1, d2, 1.0F, f, f1, f2);
	}

	public EntityFXHoneydust(World world, double d, double d1, double d2, float f, float f1, float f2, float f3) {
		super(world, d, d1, d2, f, f1, f2, f3);
		particleRed = 0.9F + (world.rand.nextFloat() * 0.2F);
		particleGreen = 0.75F + (world.rand.nextFloat() * 0.2F);
		particleBlue = 0F + (world.rand.nextFloat() * 0.2F);
	}
}
