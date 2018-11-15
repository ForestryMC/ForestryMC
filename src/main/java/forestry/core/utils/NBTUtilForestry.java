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

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

import io.netty.buffer.Unpooled;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class NBTUtilForestry {

	public enum EnumNBTType {

		END(NBTTagEnd.class),
		BYTE(NBTTagByte.class),
		SHORT(NBTTagShort.class),
		INT(NBTTagInt.class),
		LONG(NBTTagLong.class),
		FLOAT(NBTTagFloat.class),
		DOUBLE(NBTTagDouble.class),
		BYTE_ARRAY(NBTTagByteArray.class),
		STRING(NBTTagString.class),
		LIST(NBTTagList.class),
		COMPOUND(NBTTagCompound.class),
		INT_ARRAY(NBTTagIntArray.class);
		public static final EnumNBTType[] VALUES = values();
		public final Class<? extends NBTBase> classObject;

		EnumNBTType(Class<? extends NBTBase> c) {
			this.classObject = c;
		}
	}

	public static <T extends NBTBase> NBTList<T> getNBTList(NBTTagCompound nbt, String tag, EnumNBTType type) {
		NBTTagList nbtList = nbt.getTagList(tag, type.ordinal());
		return new NBTList<>(nbtList);
	}

	public static long[] getLongArray(NBTBase nbt) {
		if (!(nbt instanceof NBTTagLongArray)) {
			return new long[0];
		}
		return ObfuscationReflectionHelper.getPrivateValue(NBTTagLongArray.class, (NBTTagLongArray) nbt, 0);
	}

	public static class NBTList<T extends NBTBase> extends ForwardingList<T> {

		private final ArrayList<T> backingList;
		private final NBTTagList nbtList;

		public NBTList(NBTTagList nbtList) {
			this.nbtList = nbtList;
			backingList = ObfuscationReflectionHelper.getPrivateValue(NBTTagList.class, nbtList, 1);
		}

		@Override
		protected List<T> delegate() {
			return backingList;
		}

	}

	public static NBTTagCompound writeStreamableToNbt(IStreamable streamable, NBTTagCompound nbt) {
		PacketBufferForestry data = new PacketBufferForestry(Unpooled.buffer());
		streamable.writeData(data);

		byte[] bytes = new byte[data.readableBytes()];
		data.getBytes(0, bytes);
		nbt.setByteArray("dataBytes", bytes);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	public static void readStreamableFromNbt(IStreamable streamable, NBTTagCompound nbt) {
		if (nbt.hasKey("dataBytes")) {
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
