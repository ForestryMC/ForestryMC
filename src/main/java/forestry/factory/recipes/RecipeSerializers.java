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
package forestry.factory.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraftforge.fluids.FluidStack;

public class RecipeSerializers {

	static <E> void write(FriendlyByteBuf buffer, NonNullList<E> list, BiConsumer<FriendlyByteBuf, E> consumer) {
		buffer.writeVarInt(list.size());

		for (E e : list) {
			consumer.accept(buffer, e);
		}
	}

	static <E> NonNullList<E> read(FriendlyByteBuf buffer, Function<FriendlyByteBuf, E> reader) {
		NonNullList<E> list = NonNullList.create();
		int size = buffer.readVarInt();

		for (int i = 0; i < size; i++) {
			list.add(reader.apply(buffer));
		}

		return list;
	}

	public static FluidStack deserializeFluid(JsonObject object) {
		return FluidStack.loadFluidStackFromNBT((CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, object));
	}

	public static JsonObject serializeFluid(FluidStack fluid) {
		return (JsonObject) Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, fluid.writeToNBT(new CompoundTag()));
	}

	public static ItemStack item(JsonObject object) {
		return ItemStack.of((CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, object));
	}

	public static JsonObject item(ItemStack stack) {
		return (JsonObject) Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, stack.serializeNBT());
	}

	public static Ingredient deserialize(JsonElement resource) {
		if (resource.isJsonArray() && resource.getAsJsonArray().size() == 0) {
			return Ingredient.EMPTY;
		}

		return Ingredient.fromJson(resource);
	}
}
