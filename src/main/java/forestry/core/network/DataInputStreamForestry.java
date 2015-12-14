package forestry.core.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.Collection;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameData;

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
				itemstack.stackTagCompound = readNBTTagCompound();
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

	public void readItemStacks(Collection<ItemStack> itemStacks) throws IOException {
		itemStacks.clear();

		int stackCount = readVarInt();
		for (int i = 0; i < stackCount; i++) {
			itemStacks.add(readItemStack());
		}
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
				throw new InvalidObjectException("Failed to read Streamable for class " + streamableClass + " with error " + e);
			}
		}
		return null;
	}

	public <T extends IStreamable> void readStreamables(List<T> outputList, Class<T> streamableClass) throws IOException {
		outputList.clear();
		int length = readVarInt();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				T streamable = readStreamable(streamableClass);
				outputList.add(streamable);
			}
		}
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
				throw new InvalidObjectException("VarInt too big");
			}
		} while ((b0 & 128) == 128);

		return varInt;
	}

	public <T extends Enum<T>> T readEnum(T[] enumValues) throws IOException {
		int ordinal;
		if (enumValues.length <= 256) {
			ordinal = readByte();
		} else {
			ordinal = readVarInt();
		}
		return enumValues[ordinal];
	}

	public NBTTagCompound readNBTTagCompound() throws IOException {
		int length = readVarInt();

		if (length < 0) {
			return null;
		} else {
			byte[] compressed = new byte[length];
			readFully(compressed);
			return CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressed));
		}
	}

	public FluidStack readFluidStack() throws IOException {
		int fluidId = readVarInt();
		Fluid fluid = FluidRegistry.getFluid(fluidId);
		if (fluid == null) {
			return null;
		}

		int amount = readVarInt();
		return new FluidStack(fluid, amount);
	}
}
