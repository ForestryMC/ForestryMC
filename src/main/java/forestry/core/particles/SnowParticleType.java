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
package forestry.core.particles;

import javax.annotation.Nonnull;

import net.minecraft.particles.ParticleType;

import com.mojang.serialization.Codec;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowParticleType extends ParticleType<SnowParticleData> {
	public SnowParticleType() {
		super(false, SnowParticleData.DESERIALIZER);
	}

	@Nonnull
	@Override
	public Codec<SnowParticleData> codec() {
		return SnowParticleData.CODEC;
	}
}
