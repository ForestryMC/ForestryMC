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

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BeeTargetParticleData implements IParticleData {

	public static final IDeserializer<BeeTargetParticleData> DESERIALIZER = new IDeserializer<BeeTargetParticleData>() {
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
		public BeeTargetParticleData fromNetwork(@Nonnull ParticleType<BeeTargetParticleData> type, PacketBuffer buf) {
			return new BeeTargetParticleData(buf.readInt(), buf.readInt());
		}
	};

	public static Codec<BeeTargetParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(Codec.INT.fieldOf("entity").forGetter(data -> data.entity), Codec.INT.fieldOf("color").forGetter(data -> data.color)).apply(val, BeeTargetParticleData::new));

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
	public void writeToNetwork(@Nonnull PacketBuffer buffer) {
		buffer.writeLong(entity);
		buffer.writeInt(color);
	}

	@Nonnull
	@Override
	public String writeToString() {
		return String.format(Locale.ROOT, "%s %d %d", getType().getRegistryName(), entity, color);
	}
}
