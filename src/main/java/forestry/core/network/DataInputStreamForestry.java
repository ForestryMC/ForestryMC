package forestry.core.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameData;
import forestry.core.proxy.Proxies;

public class DataInputStreamForestry extends DataInputStream {

	public DataInputStreamForestry(InputStream in) {
		super(in);
	}

	public ItemStack readItemStack() throws IOException {
		ItemStack itemstack = null;
		String itemName = readUTF();

		if (!itemName.isEmpty()) {
			Item item = GameData.getItemRegistry().getRaw(itemName);
			byte stackSize = readByte();
			int meta = readVarInt();
			itemstack = new ItemStack(item, stackSize, meta);

			if (item.isDamageable() || item.getShareTag()) {
				itemstack.setTagCompound(readNBTTagCompound());
			}
		}

		return itemstack;
	}

	public ItemStack[] readItemStacks() throws IOException {
		int stackCount = readVarInt();

		ItemStack[] itemStacks = new ItemStack[stackCount];
		for (int i = 0; i < stackCount; i++) {
			itemStacks[i] = readItemStack();
		}

		return itemStacks;
	}

	public void readInventory(IInventory inventory) throws IOException {
		int size = readVarInt();

		for (int i = 0; i < size; i++) {
			ItemStack stack = readItemStack();
			inventory.setInventorySlotContents(i, stack);
		}
	}

	public <T extends IStreamable> T readStreamable(Class<T> streamableClass) throws IOException {
		if (readBoolean()) {
			try {
				T streamable = streamableClass.newInstance();
				streamable.readData(this);
				return streamable;
			} catch (ReflectiveOperationException e) {
				Proxies.log.severe("Failed to read Streamable for class " + streamableClass + " with error " + e);
			}
		}
		return null;
	}

	public <T extends IStreamable> List<T> readStreamables(Class<T> streamableClass) throws IOException {
		int length = readVarInt();
		List<T> streamables = new ArrayList<T>(length);
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				T streamable = readStreamable(streamableClass);
				streamables.add(streamable);
			}
		}
		return streamables;
	}

	/**
	 * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
	 * bit dictates whether another byte should be read.
	 */
	public int readVarInt() throws IOException {
		int varInt = 0;
		int size = 0;
		byte b0;

		do {
			b0 = readByte();
			varInt |= (b0 & 127) << size++ * 7;

			if (size > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((b0 & 128) == 128);

		return varInt;
	}

	public NBTTagCompound readNBTTagCompound() throws IOException {
		int length = readVarInt();

		if (length < 0) {
			return null;
		} else {
			return CompressedStreamTools.readCompressed(in);
		}
	}
}
