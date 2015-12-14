package forestry.core.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameData;

public class DataOutputStreamForestry extends DataOutputStream {

	public DataOutputStreamForestry(OutputStream out) {
		super(out);
	}

	public void writeItemStack(ItemStack itemstack) throws IOException {
		if (itemstack == null) {
			writeUTF("");
		} else {
			writeUTF(GameData.getItemRegistry().getNameForObject(itemstack.getItem()));
			writeByte(itemstack.stackSize);
			writeVarInt(itemstack.getItemDamage());

			if (itemstack.getItem().isDamageable() || itemstack.getItem().getShareTag()) {
				writeNBTTagCompound(itemstack.stackTagCompound);
			}
		}
	}

	public void writeItemStacks(ItemStack[] itemStacks) throws IOException {
		writeVarInt(itemStacks.length);
		for (ItemStack itemstack : itemStacks) {
			writeItemStack(itemstack);
		}
	}

	public void writeItemStacks(Collection<ItemStack> itemStacks) throws IOException {
		writeVarInt(itemStacks.size());
		for (ItemStack itemstack : itemStacks) {
			writeItemStack(itemstack);
		}
	}

	public void writeInventory(IInventory inventory) throws IOException {
		int size = inventory.getSizeInventory();
		writeVarInt(size);

		for (int i = 0; i < size; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			writeItemStack(stack);
		}
	}

	public void writeStreamable(IStreamable streamable) throws IOException {
		if (streamable != null) {
			writeBoolean(true);
			streamable.writeData(this);
		} else {
			writeBoolean(false);
		}
	}

	public <T extends IStreamable> void writeStreamables(List<T> streamables) throws IOException {
		if (streamables == null) {
			writeVarInt(0);
		} else {
			writeVarInt(streamables.size());
			for (IStreamable streamable : streamables) {
				writeStreamable(streamable);
			}
		}
	}

	/**
	 * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of
	 * each such byte only 7 bits will be used to describe the actual value since its most significant bit dictates
	 * whether the next byte is part of that same int. Micro-optimization for int values that are expected to have
	 * values below 128.
	 */
	public void writeVarInt(int varInt) throws IOException {
		while ((varInt & -128) != 0) {
			writeByte(varInt & 127 | 128);
			varInt >>>= 7;
		}

		writeByte(varInt);
	}

	public <T extends Enum<T>> void writeEnum(T enumValue, T[] enumValues) throws IOException {
		if (enumValues.length <= 256) {
			writeByte(enumValue.ordinal());
		} else {
			writeVarInt(enumValue.ordinal());
		}
	}

	public void writeNBTTagCompound(NBTTagCompound nbttagcompound) throws IOException {
		if (nbttagcompound == null) {
			writeVarInt(-1);
		} else {
			byte[] compressed = CompressedStreamTools.compress(nbttagcompound);
			writeVarInt((short) compressed.length);
			write(compressed);
		}
	}

	public void writeFluidStack(FluidStack fluidStack) throws IOException {
		if (fluidStack == null) {
			writeVarInt(-1);
		} else {
			writeVarInt(fluidStack.getFluidID());
			writeVarInt(fluidStack.amount);
		}
	}
}
