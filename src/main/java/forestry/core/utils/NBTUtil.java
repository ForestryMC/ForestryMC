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
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class NBTUtil {

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

		public static EnumNBTType fromClass(Class<? extends NBTBase> c) {
			for (EnumNBTType type : VALUES) {
				if (type.classObject == c) {
					return type;
				}
			}
			return null;
		}

	}

	public static <T extends NBTBase> NBTList<T> getNBTList(NBTTagCompound nbt, String tag, EnumNBTType type) {
		NBTTagList nbtList = nbt.getTagList(tag, type.ordinal());
		return new NBTList<>(nbtList);
	}

	public static class NBTList<T extends NBTBase> extends ForwardingList<T> {

		private final ArrayList<T> backingList;
		private final NBTTagList nbtList;

		public NBTList(NBTTagList nbtList) {
			this.nbtList = nbtList;
			backingList = ObfuscationReflectionHelper.getPrivateValue(NBTTagList.class, nbtList, 0);
		}

		@Override
		protected List<T> delegate() {
			return backingList;
		}

	}
}
