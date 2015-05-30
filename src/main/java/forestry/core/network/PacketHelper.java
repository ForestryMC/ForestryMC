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
package forestry.core.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.registry.GameData;

import forestry.core.proxy.Proxies;

public class PacketHelper {

	public static ItemStack readItemStack(DataInputStream data) throws IOException {
		ItemStack itemstack = null;
		String itemName = data.readUTF();

		if (!itemName.isEmpty()) {
			Item item = GameData.getItemRegistry().getRaw(itemName);
			byte stackSize = data.readByte();
			short meta = data.readShort();
			itemstack = new ItemStack(item, stackSize, meta);

			if (item.isDamageable() || item.getShareTag()) {
				itemstack.stackTagCompound = readNBTTagCompound(data);
			}
		}

		return itemstack;
	}

	public static void writeItemStack(ItemStack itemstack, DataOutputStream data) throws IOException {

		if (itemstack == null) {
			data.writeUTF("");
		} else {
			data.writeUTF(GameData.getItemRegistry().getNameForObject(itemstack.getItem()));
			data.writeByte(itemstack.stackSize);
			data.writeShort(itemstack.getItemDamage());

			if (itemstack.getItem().isDamageable() || itemstack.getItem().getShareTag()) {
				writeNBTTagCompound(itemstack.stackTagCompound, data);
			}
		}
	}

	public static ItemStack[] readItemStacks(DataInputStream data) throws IOException {
		int stackCount = data.readShort();
		ItemStack[] itemStacks = new ItemStack[stackCount];
		for (int i = 0; i < stackCount; i++) {
			itemStacks[i] = PacketHelper.readItemStack(data);
		}
		return itemStacks;
	}

	public static void writeItemStacks(ItemStack[] itemStacks, DataOutputStream data) throws IOException {
		data.writeShort(itemStacks.length);
		for (ItemStack itemstack : itemStacks) {
			PacketHelper.writeItemStack(itemstack, data);
		}
	}

	public static void writeInventory(IInventory inventory, DataOutputStream data) throws IOException {
		int size = inventory.getSizeInventory();
		data.writeShort(size);

		for (int i = 0; i < size; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			PacketHelper.writeItemStack(stack, data);
		}
	}

	public static void readInventory(IInventory inventory, DataInputStream data) throws IOException {
		int size = data.readShort();

		for (int i = 0; i < size; i++) {
			ItemStack stack = PacketHelper.readItemStack(data);
			inventory.setInventorySlotContents(i, stack);
		}
	}

	public static <T extends IStreamable> void writeStreamables(List<T> streamables, DataOutputStream data) throws IOException {
		if (streamables == null) {
			data.writeInt(0);
		} else {
			data.writeInt(streamables.size());
			for (IStreamable streamable : streamables) {
				if (streamable != null) {
					data.writeBoolean(true);
					streamable.writeData(data);
				} else {
					data.writeBoolean(false);
				}
			}
		}
	}

	public static <T extends IStreamable> List<T> readStreamables(Class<T> streamableClass, DataInputStream data) throws IOException {
		int length = data.readInt();
		List<T> streamables = new ArrayList<T>(length);

		try {
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					if (data.readBoolean()) {
						T streamable = streamableClass.newInstance();
						streamable.readData(data);
						streamables.add(streamable);
					} else {
						streamables.add(null);
					}
				}
			}
		} catch (ReflectiveOperationException e) {
			Proxies.log.severe("Failed to read Streamables for class " + streamableClass + " with error " + e);
		}
		return streamables;
	}

	private static NBTTagCompound readNBTTagCompound(DataInputStream data) throws IOException {

		short length = data.readShort();

		if (length < 0) {
			return null;
		} else {
			byte[] compressed = new byte[length];
			data.readFully(compressed);
			return CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressed));
		}
	}

	private static void writeNBTTagCompound(NBTTagCompound nbttagcompound, DataOutputStream data) throws IOException {

		if (nbttagcompound == null) {
			data.writeShort(-1);
		} else {
			byte[] compressed = CompressedStreamTools.compress(nbttagcompound);
			data.writeShort((short) compressed.length);
			data.write(compressed);
		}
	}
}
