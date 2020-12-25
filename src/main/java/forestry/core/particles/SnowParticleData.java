/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class SnowParticleData implements IParticleData {

    public static final IDeserializer<SnowParticleData> DESERIALIZER = new IDeserializer<SnowParticleData>() {
        @Nonnull
        @Override
        public SnowParticleData deserialize(
                @Nonnull ParticleType<SnowParticleData> type,
                @Nonnull StringReader reader
        ) throws CommandSyntaxException {
            reader.expect(' ');
            double particleStartX = reader.readDouble();
            reader.expect(' ');
            double particleStartY = reader.readDouble();
            reader.expect(' ');
            double particleStartZ = reader.readDouble();
            reader.expect(' ');
            return new SnowParticleData(
                    particleStartX,
                    particleStartY,
                    particleStartZ
            );
        }

        @Override
        public SnowParticleData read(@Nonnull ParticleType<SnowParticleData> type, PacketBuffer buf) {
            return new SnowParticleData(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        }
    };
    public static final Codec<SnowParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(
            Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.getX()),
            Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.getY()),
            Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.getY())
    ).apply(val, SnowParticleData::new));

    public final Vector3d particleStart;

    public SnowParticleData(
            double particleStartX,
            double particleStartY,
            double particleStartZ
    ) {
        this.particleStart = new Vector3d(particleStartX, particleStartY, particleStartZ);
    }

    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return CoreParticles.SNOW_PARTICLE.getParticleType();
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer) {
        buffer.writeDouble(particleStart.getX());
        buffer.writeDouble(particleStart.getY());
        buffer.writeDouble(particleStart.getZ());
    }

    @Nonnull
    @Override
    public String getParameters() {
        return String.format(
                Locale.ROOT,
                "%s %2f %2f %2f",
                getType().getRegistryName(),
                particleStart.getX(),
                particleStart.getY(),
                particleStart.getZ()
        );
    }
}
