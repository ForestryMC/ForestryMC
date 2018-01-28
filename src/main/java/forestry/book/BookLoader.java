package forestry.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.Language;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import forestry.api.book.BookContent;
import forestry.api.book.IBookEntryBuilder;
import forestry.api.book.IForesterBook;
import forestry.book.data.BookCategoryDeserializer;
import forestry.book.data.BookContentDeserializer;
import forestry.book.data.EntryData;
import forestry.book.data.content.CarpenterContent;
import forestry.book.data.content.CraftingContent;
import forestry.book.data.content.ImageContent;
import forestry.book.data.content.MutationContent;
import forestry.book.data.content.StructureContent;
import forestry.book.data.content.TextContent;
import forestry.book.pages.ContentPageLoader;
import forestry.core.utils.JsonUtil;
import forestry.core.utils.Log;
import forestry.modules.ModuleHelper;

public class BookLoader implements IResourceManagerReloadListener {
	//
	private static final String BOOK_LOCATION = "forestry:manual/";
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(BookContent.class, new BookContentDeserializer())
		.registerTypeAdapter(BookCategory.class, new BookCategoryDeserializer())
		.registerTypeAdapter(ResourceLocation.class, (JsonDeserializer<ResourceLocation>) (json, typeOfT, context) -> new ResourceLocation(JsonUtils.getString(json, "location")))
		.registerTypeAdapter(ItemStack.class, (JsonDeserializer<ItemStack>) (json, typeOfT, context) -> JsonUtil.deserializeItemStack(json.getAsJsonObject(), ItemStack.EMPTY))
		.registerTypeAdapter(Entries.class, new EntriesDeserializer())
		//.registerTypeAdapter(CraftingData.class, new CraftingData.Deserializer())
		.create();

	public static final Map<String, Class<? extends BookContent>> contentByType = new HashMap<>();

	//
	public static final BookLoader INSTANCE = new BookLoader();

	@Nullable
	public ForesterBook book;

	private BookLoader() {
		registerType("text", TextContent.class);
		registerType("image", ImageContent.class);
		registerType("crafting", CraftingContent.class);
		registerType("mutation", MutationContent.class);
		registerType("carpenter", CarpenterContent.class);
		registerType("structure", StructureContent.class);
	}

	public static void registerType(String name, Class<? extends BookContent> clazz){
		contentByType.put(name, clazz);
	}

	@Nullable
	public static Class<? extends BookContent> getType(String name){
		return contentByType.get(name);
	}

	@Nullable
	public IForesterBook getBook() {
		return book;
	}

	@SuppressWarnings("unchecked")
	public IForesterBook loadBook(){
		if(book != null){
			return book;
		}
		book = new ForesterBook();
		IResource resourceCategory = getResource(new ResourceLocation(BOOK_LOCATION + "categories.json"));
		if(resourceCategory == null){
			throw new NullPointerException();
		}
		BookCategory[] categories;
		try(InputStream inputStream = resourceCategory.getInputStream()) {
			categories = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), BookCategory[].class);
		}catch(Exception e){
			categories = new BookCategory[0];
		}
		book.addCaregories(categories);
		try {
			for (BookCategory category : categories) {
				ResourceLocation entriesLocation = new ResourceLocation(BOOK_LOCATION + "entries/" + category.getName() + ".json");
				Set<String> entryNames = new HashSet<>();
				for (IResource entryResource : getResources(entriesLocation)) {
					try (InputStream stream = entryResource.getInputStream()) {
						Entries entries = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Entries.class);
						entryNames.addAll(entries.names);
					} catch (IOException e) {
						Log.error("Failed to parse entries file.{0}", e);
					}

				}
				Map<String, EntryData> entries = new HashMap<>();
				for (String entry : entryNames) {
					loadData(entries, entry);
				}
				for (String entry : entryNames) {
					EntryData data = entries.get(entry);
					IBookEntryBuilder builder = category.createEntry(entry);
					builder.setStack(data.icon);
					builder.setLoader(ContentPageLoader.INSTNACE);
					builder.setContent(data.content);
					builder.setTitle(data.title);
					for (String subEntry : data.subEntries) {
						EntryData subData = entries.get(subEntry);
						if (subData != null) {
							IBookEntryBuilder subBuilder = builder.addSubEntry(subEntry, subData.icon);
							subBuilder.setLoader(ContentPageLoader.INSTNACE);
							subBuilder.setContent(subData.content);
							subBuilder.setTitle(subData.title);
						}
					}
					builder.addToCategory();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return book;
	}

	private void loadData(Map<String, EntryData> entries, String entry){
		IResource entryResource = getResource(getResourceLocation(entry + ".json"));
		if (entryResource == null) {
			return;
		}
		try (InputStream stream = entryResource.getInputStream()) {
			EntryData data = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), EntryData.class);
			if (data != null) {
				entries.put(entry, data);
				for(String subEntry : data.subEntries){
					loadData(entries, subEntry);
				}
			}
		} catch (Exception e) {
			Log.error("Failed to parse entry file {}:{}", entryResource.getResourceLocation(), e);
		}
	}

	@Nullable
	public static ResourceLocation getResourceLocation(String path) {
		if(path == null) {
			return null;
		}
		if(!path.contains(":")) {
			Language currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
			String langPath = currentLanguage.getLanguageCode();
			String defaultLangPath = "en_US";

			ResourceLocation res = new ResourceLocation(BOOK_LOCATION + langPath + "/" + path);
			if(resourceExists(res)) {
				return res;
			}
			res = new ResourceLocation(BOOK_LOCATION + defaultLangPath + "/" + path);
			if(resourceExists(res)) {
				return res;
			}
			res = new ResourceLocation(BOOK_LOCATION + path);
			if(resourceExists(res)) {
				return res;
			}
			return null;
		} else {
			ResourceLocation res = new ResourceLocation(path);
			if(resourceExists(res)) {
				return res;
			}
			return null;
		}
	}

	@Nullable
	public static IResource getResource(@Nullable ResourceLocation location){
		if(location == null) {
			return null;
		}
		try {
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			return resourceManager.getResource(location);
		} catch(IOException e) {
			return null;
		}
	}

	private static List<IResource> getResources(@Nullable ResourceLocation location){
		if(location == null) {
			return Collections.emptyList();
		}
		try {
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			return resourceManager.getAllResources(location);
		} catch(IOException e) {
			return Collections.emptyList();
		}
	}

	public static boolean resourceExists(@Nullable ResourceLocation location) {
		if(location == null) {
			return false;
		}
		try {
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			resourceManager.getResource(location);
			return true;
		} catch(IOException e) {
			return false;
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		book = null;
	}

	private static class Entries{
		public Set<String> names = new HashSet<>();
	}

	private static class EntriesDeserializer implements JsonDeserializer<Entries>{
		@Override
		public Entries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			Entries entries = new Entries();
			parseElement(entries, json);
			return entries;
		}

		private void parseElement(Entries entries, JsonElement element){
			if(element.isJsonArray()){
				JsonArray array = element.getAsJsonArray();
				for(JsonElement arrayElement : array){
					if(arrayElement.isJsonPrimitive()){
						JsonPrimitive primitive = arrayElement.getAsJsonPrimitive();
						if(primitive.isString()){
							entries.names.add(primitive.getAsString());
						}
					}
				}
			}else {
				JsonObject object = element.getAsJsonObject();
				for(Map.Entry<String, JsonElement> member : object.entrySet()){
					if(ModuleHelper.isEnabled(member.getKey())){
						parseElement(entries, member.getValue());
					}
				}
			}
		}
	}
}
