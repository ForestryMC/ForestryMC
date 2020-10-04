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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class BeeParticleData implements IParticleData {

    public static final IDeserializer<BeeParticleData> DESERIALIZER = new IDeserializer<BeeParticleData>() {
        @Nonnull
        @Override
        public BeeParticleData deserialize(
                @Nonnull ParticleType<BeeParticleData> type,
                @Nonnull StringReader reader
        ) throws CommandSyntaxException {
            reader.expect(' ');
            double particleStartX = reader.readDouble();
            reader.expect(' ');
            double particleStartY = reader.readDouble();
            reader.expect(' ');
            double particleStartZ = reader.readDouble();
            reader.expect(' ');
            long direction = reader.readLong();
            reader.expect(' ');
            int color = reader.readInt();
            return new BeeParticleData(
                    particleStartX,
                    particleStartY,
                    particleStartZ,
                    direction,
                    color
            );
        }

        @Override
        public BeeParticleData read(@Nonnull ParticleType<BeeParticleData> type, PacketBuffer buf) {
            return new BeeParticleData(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readLong(),
                    buf.readInt()
            );
        }
    };
    public static final Codec<BeeParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(
            Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.getX()),
            Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.getY()),
            Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.getY()),
            Codec.LONG.fieldOf("direction").forGetter(data -> data.direction.toLong()),
            Codec.INT.fieldOf("color").forGetter(data -> data.color)
    ).apply(val, BeeParticleData::new));

    public final Vector3d particleStart;
    public final BlockPos direction;
    public final int color;

    public BeeParticleData(
            double particleStartX,
            double particleStartY,
            double particleStartZ,
            long direction,
            int color
    ) {
        this.particleStart = new Vector3d(particleStartX, particleStartY, particleStartZ);
        this.direction = BlockPos.fromLong(direction);
        this.color = color;
    }

    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return ApicultureParticles.BEE_EXPLORER_PARTICLE.getParticleType();
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer) {
        buffer.writeDouble(particleStart.getX());
        buffer.writeDouble(particleStart.getY());
        buffer.writeDouble(particleStart.getZ());
        buffer.writeLong(direction.toLong());
        buffer.writeInt(color);
    }

    @Nonnull
    @Override
    public String getParameters() {
        return String.format(
                Locale.ROOT,
                "%s %d %d %d %.2f %.2f %.2f",
                getType().getRegistryName(),
                particleStart.getX(),
                particleStart.getY(),
                particleStart.getZ(),
                direction.getX(),
                direction.getY(),
                direction.getZ(),
                color
        );
    }
}
