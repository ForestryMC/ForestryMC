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

import javax.annotation.Nonnull;
import java.util.Locale;

import deleteme.RegistryNameFinder;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BeeTargetParticleData implements ParticleOptions {

	public static final Deserializer<BeeTargetParticleData> DESERIALIZER = new Deserializer<>() {
		@Nonnull
		@Override
		public BeeTargetParticleData fromCommand(@Nonnull ParticleType<BeeTargetParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			int entityId = reader.readInt();
			reader.expect(' ');
			int color = reader.readInt();
			return new BeeTargetParticleData(entityId, color);
		}

		@Override
		public BeeTargetParticleData fromNetwork(@Nonnull ParticleType<BeeTargetParticleData> type, FriendlyByteBuf buf) {
			return new BeeTargetParticleData(buf.readInt(), buf.readInt());
		}
	};

	public static Codec<BeeTargetParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("entity").forGetter(data -> data.entity),
			Codec.INT.fieldOf("color").forGetter(data -> data.color)
	).apply(instance, BeeTargetParticleData::new));

	public final int entity;
	public final int color;

	public BeeTargetParticleData(int entity, int color) {
		this.entity = entity;
		this.color = color;
	}

	public BeeTargetParticleData(Entity entity, int color) {
		this.entity = entity.getId();
		this.color = color;
	}

	@Nonnull
	@Override
	public ParticleType<?> getType() {
		return ApicultureParticles.BEE_TARGET_ENTITY_PARTICLE.getParticleType();
	}

	@Override
	public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
		buffer.writeLong(entity);
		buffer.writeInt(color);
	}

	@Nonnull
	@Override
	public String writeToString() {
		return String.format(Locale.ROOT, "%s %d %d", RegistryNameFinder.getRegistryName(getType()), entity, color);
	}
}
