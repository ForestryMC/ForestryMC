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
import java.util.Locale;

import deleteme.RegistryNameFinder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.Vec3;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions.Deserializer;

public class SnowParticleData implements ParticleOptions {

	public static final Deserializer<SnowParticleData> DESERIALIZER = new Deserializer<SnowParticleData>() {
		@Nonnull
		@Override
		public SnowParticleData fromCommand(@Nonnull ParticleType<SnowParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			double particleStartX = reader.readDouble();
			reader.expect(' ');
			double particleStartY = reader.readDouble();
			reader.expect(' ');
			double particleStartZ = reader.readDouble();
			reader.expect(' ');
			return new SnowParticleData(particleStartX, particleStartY, particleStartZ);
		}

		@Override
		public SnowParticleData fromNetwork(@Nonnull ParticleType<SnowParticleData> type, FriendlyByteBuf buf) {
			return new SnowParticleData(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}
	};
	public static final Codec<SnowParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.x()), Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.y()), Codec.DOUBLE.fieldOf("particleStart").forGetter(data -> data.particleStart.y())).apply(val, SnowParticleData::new));

	public final Vec3 particleStart;

	public SnowParticleData(double particleStartX, double particleStartY, double particleStartZ) {
		this.particleStart = new Vec3(particleStartX, particleStartY, particleStartZ);
	}

	@Nonnull
	@Override
	public ParticleType<?> getType() {
		return CoreParticles.SNOW_PARTICLE.getParticleType();
	}

	@Override
	public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
		buffer.writeDouble(particleStart.x());
		buffer.writeDouble(particleStart.y());
		buffer.writeDouble(particleStart.z());
	}

	@Nonnull
	@Override
	public String writeToString() {
		return String.format(Locale.ROOT, "%s %2f %2f %2f", RegistryNameFinder.getRegistryName(getType()), particleStart.x(), particleStart.y(), particleStart.z());
	}
}
