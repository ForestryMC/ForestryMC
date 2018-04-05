package forestry.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.JsonUtils;

public class JsonUtil {
	private JsonUtil() {
	}

	public static ItemStack deserializeItemStack(JsonObject object){
		return deserializeItemStack(object, null);
	}

	public static ItemStack deserializeItemStack(JsonObject object, @Nullable ItemStack fallback) {
		if (!object.has("item")) {
			if(fallback == null) {
				throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
			}else{
				return fallback;
			}
		}
		Item item = net.minecraft.util.JsonUtils.getItem(object, "item");
		int meta = net.minecraft.util.JsonUtils.getInt(object, "data", 0);
		ItemStack stack = new ItemStack(item, 1, meta);
		stack.setTagCompound(JsonUtils.readNBT(object, "nbt"));
		return stack;
	}
}
