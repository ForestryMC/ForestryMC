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

import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

import io.netty.buffer.Unpooled;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class NBTUtilForestry {

	public enum EnumNBTType {

		END(EndNBT.class),
		BYTE(ByteNBT.class),
		SHORT(ShortNBT.class),
		INT(IntNBT.class),
		LONG(LongNBT.class),
		FLOAT(FloatNBT.class),
		DOUBLE(DoubleNBT.class),
		BYTE_ARRAY(ByteArrayNBT.class),
		STRING(StringNBT.class),
		LIST(ListNBT.class),
		COMPOUND(CompoundNBT.class),
		INT_ARRAY(IntArrayNBT.class);
		public static final EnumNBTType[] VALUES = values();
		public final Class<? extends INBT> classObject;

		EnumNBTType(Class<? extends INBT> c) {
			this.classObject = c;
		}
	}

	public static <T extends INBT> NBTList<T> getNBTList(CompoundNBT nbt, String tag, EnumNBTType type) {
		ListNBT nbtList = nbt.getList(tag, type.ordinal());
		return new NBTList<>(nbtList);
	}

	public static long[] getLongArray(INBT nbt) {
		if (!(nbt instanceof LongArrayNBT)) {
			return new long[0];
		}
		return ((LongArrayNBT) nbt).getAsLongArray();
	}

	public static class NBTList<T extends INBT> extends ForwardingList<T> {

		private final ArrayList<T> backingList;

		public NBTList(ListNBT nbtList) {
			//TODO shouldn't need this here
			backingList = ObfuscationReflectionHelper.getPrivateValue(ListNBT.class, nbtList, 1);
		}

		@Override
		protected List<T> delegate() {
			return backingList;
		}

	}

	public static CompoundNBT writeStreamableToNbt(IStreamable streamable, CompoundNBT nbt) {
		PacketBufferForestry data = new PacketBufferForestry(Unpooled.buffer());
		streamable.writeData(data);

		byte[] bytes = new byte[data.readableBytes()];
		data.getBytes(0, bytes);
		nbt.putByteArray("dataBytes", bytes);
		return nbt;
	}

	@OnlyIn(Dist.CLIENT)
	public static void readStreamableFromNbt(IStreamable streamable, CompoundNBT nbt) {
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
