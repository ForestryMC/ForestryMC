package forestry.factory.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class RecipeSerializers {

    static <E> void write(PacketBuffer buffer, NonNullList<E> list, BiConsumer<PacketBuffer, E> consumer) {
        buffer.writeVarInt(list.size());

        for (E e : list) {
            consumer.accept(buffer, e);
        }
    }

    static <E> NonNullList<E> read(PacketBuffer buffer, Function<PacketBuffer, E> reader) {
        NonNullList<E> list = NonNullList.create();
        int size = buffer.readVarInt();

        for (int i = 0; i < size; i++) {
            list.add(reader.apply(buffer));
        }

        return list;
    }

    public static FluidStack deserializeFluid(JsonObject object) {
        ResourceLocation fluidName = new ResourceLocation(JSONUtils.getString(object, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
        FluidStack stack = new FluidStack(fluid, JSONUtils.getInt(object, "amount", 0));

        if (object.has("tag")) {
            stack.setTag((CompoundNBT) Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, object.get("tag"))
                    .copy());
        }

        return stack;
    }

    public static JsonObject serializeFluid(FluidStack fluid) {
        JsonObject object = new JsonObject();
        object.addProperty("fluid", fluid.getFluid().getRegistryName().toString());
        object.addProperty("amount", fluid.getAmount());

        if (fluid.hasTag()) {
            object.add("Tag", Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, fluid.getTag()));
        }

        return object;
    }

    public static JsonObject item(ItemStack stack) {
        JsonObject object = new JsonObject();
        object.addProperty("item", stack.getItem().getRegistryName().toString());

        if (stack.getCount() > 1) {
            object.addProperty("count", stack.getCount());
        }

        return object;
    }
}
