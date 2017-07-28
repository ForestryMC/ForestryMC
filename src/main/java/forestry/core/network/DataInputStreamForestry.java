package forestry.core.network;

import javax.annotation.Nullable;
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
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.utils.ItemStackUtil;

public class DataInputStreamForestry extends DataInputStream {

	public DataInputStreamForestry(InputStream in) {
		super(in);
	}

	public ItemStack readItemStack() throws IOException {
		ItemStack itemstack = null;
		String itemName = readUTF();

		if (!itemName.isEmpty()) {
			Item item = ItemStackUtil.getItemFromRegistry(itemName);
			int stackSize = readVarInt();
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
			} catch (IllegalAccessException | InstantiationException | IOException e) {
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
	
	public <T extends IStreamable> void readStreamables(List<T> outputList, IStreamableFactory<T> factory) throws IOException {
		outputList.clear();
		int length = readVarInt();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				T streamable = readStreamable(factory);
				outputList.add(streamable);
			}
		}
	}
	
	@Nullable
	public <T extends IStreamable> T readStreamable(IStreamableFactory<T> factory) throws IOException {
		if (readBoolean()) {
			return factory.create(this);
		}
		return null;
	}
	
	public interface IStreamableFactory<T extends IStreamable> {
		T create(DataInputStreamForestry data) throws IOException;
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
			return CompressedStreamTools.read(this, new NBTSizeTracker(2097152L));
		}
	}

	public FluidStack readFluidStack() throws IOException {
		int amount = readVarInt();
		if (amount > 0) {
			String fluidName = readUTF();
			Fluid fluid = FluidRegistry.getFluid(fluidName);
			if (fluid == null) {
				return null;
			}

			return new FluidStack(fluid, amount);
		}
		return null;
	}
}
