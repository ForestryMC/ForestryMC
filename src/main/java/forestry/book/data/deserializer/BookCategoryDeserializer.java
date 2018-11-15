package forestry.book.data.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.book.BookCategory;
import forestry.core.utils.JsonUtil;

@SideOnly(Side.CLIENT)
public class BookCategoryDeserializer implements JsonDeserializer<BookCategory> {
	@Override
	public BookCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject object = json.getAsJsonObject();
		String name = JsonUtils.getString(object, "name");
		ItemStack stack = JsonUtil.deserializeItemStack(JsonUtils.getJsonObject(object, "icon"), ItemStack.EMPTY);
		BookCategory category = new BookCategory(name);
		category.setStack(stack);
		return category;
	}
}
