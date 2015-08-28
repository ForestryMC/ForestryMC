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
package forestry.core.render;

import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ParticleHelperCallback {

	@SideOnly(Side.CLIENT)
	void addHitEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta);

	@SideOnly(Side.CLIENT)
	void addDestroyEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta);
}
