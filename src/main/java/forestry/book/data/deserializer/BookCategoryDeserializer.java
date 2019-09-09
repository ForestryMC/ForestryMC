package forestry.book.data.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.book.BookCategory;
import forestry.core.utils.JsonUtil;

@OnlyIn(Dist.CLIENT)
public class BookCategoryDeserializer implements JsonDeserializer<BookCategory> {
	@Override
	public BookCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject object = json.getAsJsonObject();
		String name = JSONUtils.getString(object, "name");
		ItemStack stack = JsonUtil.deserializeItemStack(JSONUtils.getJsonObject(object, "icon"), ItemStack.EMPTY);
		BookCategory category = new BookCategory(name);
		category.setStack(stack);
		return category;
	}
}
