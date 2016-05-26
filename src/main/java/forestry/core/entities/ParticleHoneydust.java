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

import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.world.World;

public class ParticleHoneydust extends ParticleRedstone {

	public ParticleHoneydust(World world, double x, double y, double z, float f1, float f2, float f3) {
		super(world, x, y, z, 1.0F, f1, f2, f3);
		particleRed = 0.9F + world.rand.nextFloat() * 0.1F;
		particleGreen = 0.75F + world.rand.nextFloat() * 0.2F;
		particleBlue = 0F + world.rand.nextFloat() * 0.2F;
	}
}
