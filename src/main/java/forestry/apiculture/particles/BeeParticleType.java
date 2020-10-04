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
package forestry.apiculture.particles;

import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BeeParticleType extends ParticleType<BeeParticleData> {
    public BeeParticleType() {
        super(false, BeeParticleData.DESERIALIZER);
    }

    @Nonnull
    @Override
    public Codec<BeeParticleData> func_230522_e_() {
        return BeeParticleData.CODEC;
    }
}
