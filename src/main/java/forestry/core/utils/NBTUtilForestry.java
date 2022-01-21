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
package forestry.core.utils;

import com.google.common.collect.ForwardingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

import io.netty.buffer.Unpooled;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class NBTUtilForestry {

	public enum EnumNBTType {

		END(EndTag.class),
		BYTE(ByteTag.class),
		SHORT(ShortTag.class),
		INT(IntTag.class),
		LONG(LongTag.class),
		FLOAT(FloatTag.class),
		DOUBLE(DoubleTag.class),
		BYTE_ARRAY(ByteArrayTag.class),
		STRING(StringTag.class),
		LIST(ListTag.class),
		COMPOUND(CompoundTag.class),
		INT_ARRAY(IntArrayTag.class);
		public static final EnumNBTType[] VALUES = values();
		public final Class<? extends Tag> classObject;

		EnumNBTType(Class<? extends Tag> c) {
			this.classObject = c;
		}
	}

	public static <T extends Tag> NBTList<T> getNBTList(CompoundTag nbt, String tag, EnumNBTType type) {
		ListTag nbtList = nbt.getList(tag, type.ordinal());
		return new NBTList<>(nbtList);
	}

	public static class NBTList<T extends Tag> extends ForwardingList<T> {

		private final ArrayList<T> backingList;

		public NBTList(ListTag nbtList) {
			//noinspection unchecked
			backingList = new ArrayList<>((List<T>) nbtList.list);
		}

		@Override
		protected List<T> delegate() {
			return backingList;
		}

	}

	public static CompoundTag writeStreamableToNbt(IStreamable streamable, CompoundTag nbt) {
		PacketBufferForestry data = new PacketBufferForestry(Unpooled.buffer());
		streamable.writeData(data);

		byte[] bytes = new byte[data.readableBytes()];
		data.getBytes(0, bytes);
		nbt.putByteArray("dataBytes", bytes);
		return nbt;
	}

	@OnlyIn(Dist.CLIENT)
	public static void readStreamableFromNbt(IStreamable streamable, CompoundTag nbt) {
		if (nbt.contains("dataBytes")) {
			byte[] bytes = nbt.getByteArray("dataBytes");
			PacketBufferForestry data = new PacketBufferForestry(Unpooled.wrappedBuffer(bytes));
			try {
				streamable.readData(data);
			} catch (IOException e) {
				Log.error("Failed to read streamable data", e);
			}
		}
	}

}
