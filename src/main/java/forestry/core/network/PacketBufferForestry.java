package forestry.core.network;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.climate.IClimateState;
import forestry.core.climate.AbsentClimateState;
import forestry.core.climate.ClimateStateHelper;

import io.netty.buffer.ByteBuf;

public class PacketBufferForestry extends FriendlyByteBuf {
	public PacketBufferForestry(ByteBuf wrapped) {
		super(wrapped);
	}

	public String readUtf() {
		return super.readUtf(1024);
	}

	public void writeItemStacks(NonNullList<ItemStack> itemStacks) {
		writeVarInt(itemStacks.size());
		for (ItemStack stack : itemStacks) {
			writeItem(stack);
		}
	}

	public NonNullList<ItemStack> readItemStacks() {
		int stackCount = readVarInt();
		NonNullList<ItemStack> itemStacks = NonNullList.create();
		for (int i = 0; i < stackCount; i++) {
			itemStacks.add(readItem());
		}
		return itemStacks;
	}

	public void writeInventory(Container inventory) {
		int size = inventory.getContainerSize();
		writeVarInt(size);

		for (int i = 0; i < size; i++) {
			ItemStack stack = inventory.getItem(i);
			writeItem(stack);
		}
	}

	public void readInventory(Container inventory) {
		int size = readVarInt();

		for (int i = 0; i < size; i++) {
			ItemStack stack = readItem();
			inventory.setItem(i, stack);
		}
	}

	public void writeFluidStack(FluidStack fluidStack) {
		fluidStack.writeToPacket(this);
	}

	public FluidStack readFluidStack() {
		return FluidStack.readFromPacket(this);
	}

	public void writeEntityById(Entity entity) {
		writeVarInt(entity.getId());
	}

	@Nullable
	public Entity readEntityById(Level world) {
		int entityId = readVarInt();
		return world.getEntity(entityId);
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
