package forestry.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;

import net.minecraftforge.common.util.JsonUtils;

public class JsonUtil {
	private JsonUtil() {
	}

	public static ItemStack deserializeItemStack(JsonObject object, ItemStack fallback) {
		return deserializeItemStack(object, fallback, false);
	}

	public static ItemStack deserializeItemStack(JsonObject object, ItemStack fallback, boolean logError) {
		if (!object.has("item")) {
			if (logError) {
				Log.error("Unsupported icon type, currently only items are supported (add 'item' key)");
			}
			return fallback;
		}
		try {
			Item item = JSONUtils.getItem(object, "item");
			int meta = JSONUtils.getInt(object, "data", 0);
			ItemStack stack = new ItemStack(item, 1);//TODO flatten, meta);
			stack.setTag(JsonUtils.readNBT(object, "nbt"));
			return stack;
		} catch (JsonSyntaxException e) {
			if (logError) {
				Log.trace("Filed to parse item.", e);
			}
			return fallback;
		}
	}
}
