package forestry.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.GsonHelper;

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
			Item item = GsonHelper.getAsItem(object, "item");
			int count = GsonHelper.getAsInt(object, "count", 1);
			ItemStack stack = new ItemStack(item, count);
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
