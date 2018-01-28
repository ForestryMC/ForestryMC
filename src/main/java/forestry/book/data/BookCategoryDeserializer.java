package forestry.book.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;

import forestry.book.BookCategory;
import forestry.core.utils.JsonUtil;

public class BookCategoryDeserializer implements JsonDeserializer<BookCategory>{
	@Override
	public BookCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject object = json.getAsJsonObject();
		String name = JsonUtils.getString(object, "name");
		ItemStack stack = JsonUtil.deserializeItemStack(JsonUtils.getJsonObject(object, "icon"));
		BookCategory category = new BookCategory(name);
		category.setStack(stack);
		return category;
	}
}
