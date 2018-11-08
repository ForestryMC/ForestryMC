package forestry.core.network;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.climate.IClimateState;
import forestry.core.climate.AbsentClimateState;
import forestry.core.climate.ClimateStateHelper;

import io.netty.buffer.ByteBuf;

public class PacketBufferForestry extends PacketBuffer {
	public PacketBufferForestry(ByteBuf wrapped) {
		super(wrapped);
	}

	public String readString() {
		return super.readString(1024);
	}

	public void writeItemStacks(NonNullList<ItemStack> itemStacks) {
		writeVarInt(itemStacks.size());
		for (ItemStack stack : itemStacks) {
			writeItemStack(stack);
		}
	}

	public NonNullList<ItemStack> readItemStacks() throws IOException {
		int stackCount = readVarInt();
		NonNullList<ItemStack> itemStacks = NonNullList.create();
		for (int i = 0; i < stackCount; i++) {
			itemStacks.add(readItemStack());
		}
		return itemStacks;
	}

	public void writeInventory(IInventory inventory) {
		int size = inventory.getSizeInventory();
		writeVarInt(size);

		for (int i = 0; i < size; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			writeItemStack(stack);
		}
	}

	public void readInventory(IInventory inventory) throws IOException {
		int size = readVarInt();

		for (int i = 0; i < size; i++) {
			ItemStack stack = readItemStack();
			inventory.setInventorySlotContents(i, stack);
		}
	}

	public void writeFluidStack(@Nullable FluidStack fluidStack) {
		if (fluidStack == null) {
			writeVarInt(-1);
		} else {
			writeVarInt(fluidStack.amount);
			writeString(fluidStack.getFluid().getName());
		}
	}

	@Nullable
	public FluidStack readFluidStack() {
		int amount = readVarInt();
		if (amount > 0) {
			String fluidName = readString();
			Fluid fluid = FluidRegistry.getFluid(fluidName);
			if (fluid == null) {
				return null;
			}

			return new FluidStack(fluid, amount);
		}
		return null;
	}

	public void writeEntityById(Entity entity) {
		writeVarInt(entity.getEntityId());
	}

	@Nullable
	public Entity readEntityById(World world) {
		int entityId = readVarInt();
		return world.getEntityByID(entityId);
	}

	public <T extends Enum<T>> void writeEnum(T enumValue, T[] enumValues) {
		if (enumValues.length <= 256) {
			writeByte(enumValue.ordinal());
		} else {
			writeVarInt(enumValue.ordinal());
		}
	}

	public <T extends Enum<T>> T readEnum(T[] enumValues) {
		int ordinal;
		if (enumValues.length <= 256) {
			ordinal = readByte();
		} else {
			ordinal = readVarInt();
		}
		return enumValues[ordinal];
	}

	public void writeStreamable(@Nullable Object object) {
		if (object != null && object instanceof IStreamable) {
			IStreamable streamable = (IStreamable) object;
			writeBoolean(true);
			streamable.writeData(this);
		} else {
			writeBoolean(false);
		}
	}

	public void writeStreamable(@Nullable IStreamable streamable) {
		if (streamable != null) {
			writeBoolean(true);
			streamable.writeData(this);
		} else {
			writeBoolean(false);
		}
	}

	@Nullable
	public <T extends IStreamable> T readStreamable(IStreamableFactory<T> factory) throws IOException {
		if (readBoolean()) {
			return factory.create(this);
		}
		return null;
	}

	public <T extends IStreamable> void writeStreamables(@Nullable List<T> streamables) {
		if (streamables == null) {
			writeVarInt(0);
		} else {
			writeVarInt(streamables.size());
			for (IStreamable streamable : streamables) {
				writeStreamable(streamable);
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

	public interface IStreamableFactory<T extends IStreamable> {
		T create(PacketBufferForestry data) throws IOException;
	}

	public void writeClimateState(IClimateState climateState) {
		if (climateState.isPresent()) {
			writeBoolean(true);
			writeFloat(climateState.getTemperature());
			writeFloat(climateState.getHumidity());
			writeBoolean(climateState.isMutable());
		} else {
			writeBoolean(false);
		}
	}

	public IClimateState readClimateState() {
		if (readBoolean()) {
			return ClimateStateHelper.of(readFloat(), readFloat(), readBoolean());
		} else {
			return AbsentClimateState.INSTANCE;
		}
	}
}
