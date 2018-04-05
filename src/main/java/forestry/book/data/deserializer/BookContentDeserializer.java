package forestry.book.data.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import net.minecraft.util.JsonUtils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.BookContent;
import forestry.book.BookLoader;
import forestry.book.data.content.TextContent;

@SideOnly(Side.CLIENT)
public class BookContentDeserializer implements JsonDeserializer<BookContent> {
	@Override
	public BookContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject object = json.getAsJsonObject();
		String type = JsonUtils.getString(object, "type", "text");
		Class<? extends BookContent> typeClass = BookLoader.INSTANCE.getContentType(type);
		BookContent content = context.deserialize(object, typeClass == null ? TextContent.class : typeClass);
		content.type = type;
		if (content.getDataClass() != null) {
			content.data = context.deserialize(object, content.getDataClass());
		}
		content.onDeserialization();
		return content;
	}
}
