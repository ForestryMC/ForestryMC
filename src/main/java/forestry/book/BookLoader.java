package forestry.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.Language;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.BookContent;
import forestry.api.book.IBookEntryBuilder;
import forestry.api.book.IBookLoader;
import forestry.api.book.IBookPageFactory;
import forestry.api.book.IForesterBook;
import forestry.book.data.EntryData;
import forestry.book.data.content.CarpenterContent;
import forestry.book.data.content.CraftingContent;
import forestry.book.data.content.FabricatorContent;
import forestry.book.data.content.ImageContent;
import forestry.book.data.content.IndexContent;
import forestry.book.data.content.MutationContent;
import forestry.book.data.content.StructureContent;
import forestry.book.data.content.TextContent;
import forestry.book.data.deserializer.BookCategoryDeserializer;
import forestry.book.data.deserializer.BookContentDeserializer;
import forestry.book.pages.JsonPageFactory;
import forestry.core.utils.JsonUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ResourceUtil;
import forestry.modules.ModuleHelper;

@SideOnly(Side.CLIENT)
public class BookLoader implements IResourceManagerReloadListener, IBookLoader {
	public static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(BookContent.class, new BookContentDeserializer())
		.registerTypeAdapter(BookCategory.class, new BookCategoryDeserializer())
		.registerTypeAdapter(ResourceLocation.class, (JsonDeserializer<ResourceLocation>) (json, typeOfT, context) -> new ResourceLocation(JsonUtils.getString(json, "location")))
		.registerTypeAdapter(ItemStack.class, (JsonDeserializer<ItemStack>) (json, typeOfT, context) -> JsonUtil.deserializeItemStack(json.getAsJsonObject(), ItemStack.EMPTY))
		.registerTypeAdapter(Entries.class, new EntriesDeserializer())
		.create();
	public static final BookLoader INSTANCE = new BookLoader();
	private static final String BOOK_LOCATION = "forestry:manual/";
	private static final String BOOK_LOCATION_LANG = BOOK_LOCATION + "%s/%s";
	private static final String DEFAULT_LANG = "en_US";
	private final Map<String, Class<? extends BookContent>> contentByType = new HashMap<>();
	private final Map<String, IBookPageFactory> factoryByName = new HashMap<>();
	@Nullable
	private ForesterBook book = null;

	private BookLoader() {
		registerContentType("text", TextContent.class);
		registerContentType("image", ImageContent.class);
		registerContentType("crafting", CraftingContent.class);
		registerContentType("mutation", MutationContent.class);
		registerContentType("carpenter", CarpenterContent.class);
		registerContentType("structure", StructureContent.class);
		registerContentType("index", IndexContent.class);
		registerContentType("fabricator", FabricatorContent.class);
		registerPageFactory(JsonPageFactory.NAME, JsonPageFactory.INSTANCE);
	}

	@Override
	public void registerContentType(String name, Class<? extends BookContent> contentClass) {
		contentByType.put(name, contentClass);
	}

	@Override
	public void registerPageFactory(String name, IBookPageFactory factory) {
		factoryByName.put(name, factory);
	}

	@Override
	public IBookPageFactory getPageFactory(String name) {
		IBookPageFactory factory = factoryByName.get(name);
		if (factory == null) {
			return JsonPageFactory.INSTANCE;
		}
		return factory;
	}

	public IForesterBook loadBook() {
		if (book != null) {
			return book;
		}
		book = new ForesterBook();
		BookCategory[] categories = fromJson(BOOK_LOCATION + "categories.json", BookCategory[].class, new BookCategory[0]);
		if (categories != null) {
			book.addCategories(categories);
			for (BookCategory category : categories) {
				loadCategory(category);
			}
		}
		return book;
	}

	@Override
	public void invalidateBook() {
		book = null;
	}

	@Nullable
	public static IResource getResource(String path) {
		IResource resource;
		if (!path.contains(":")) {
			Language currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
			String lang = currentLanguage.getLanguageCode();

			ResourceLocation location = new ResourceLocation(String.format(BOOK_LOCATION_LANG, lang, path));
			resource = ResourceUtil.getResource(location);
			if (resource != null) {
				return resource;
			}
			location = new ResourceLocation(String.format(BOOK_LOCATION_LANG, DEFAULT_LANG, path));
			resource = ResourceUtil.getResource(location);
			if (resource != null) {
				return resource;
			}
			location = new ResourceLocation(BOOK_LOCATION + path);
			return ResourceUtil.getResource(location);
		}
		ResourceLocation location = new ResourceLocation(path);
		return ResourceUtil.getResource(location);
	}

	@Nullable
	public Class<? extends BookContent> getContentType(String name) {
		return contentByType.get(name);
	}

	private void loadCategory(BookCategory category) {
		ResourceLocation entriesLocation = new ResourceLocation(BOOK_LOCATION + "entries/" + category.getName() + ".json");
		List<String> entryNames = new LinkedList<>();
		for (IResource entryResource : ResourceUtil.getResources(entriesLocation)) {
			try (BufferedReader reader = ResourceUtil.createReader(entryResource)) {
				Entries entries = GSON.fromJson(reader, Entries.class);
				entryNames.addAll(entries.names);
			} catch (IOException e) {
				Log.error("Failed to parse entries file {}.{}", entriesLocation, e);
			} finally {
				IOUtils.closeQuietly(entryResource);
			}
		}
		Map<String, EntryData> entries = new HashMap<>(entryNames.size());
		entryNames.forEach(entry -> loadEntries(entries, entry));
		entryNames.forEach(entry -> createEntry(entries, category, entry));
	}

	private void createEntry(Map<String, EntryData> entries, BookCategory category, String entry) {
		EntryData data = entries.get(entry);
		if (data != null) {
			IBookEntryBuilder builder = category.createEntry(entry);
			builder.setStack(data.icon);
			builder.setContent(data.content);
			builder.setTitle(data.title);
			builder.setLoader(BookLoader.INSTANCE.getPageFactory(data.loader));
			for (String subEntry : data.subEntries) {
				EntryData subData = entries.get(subEntry);
				if (subData != null) {
					IBookEntryBuilder subBuilder = builder.createSubEntry(subEntry, subData.icon);
					subBuilder.setContent(subData.content);
					subBuilder.setTitle(subData.title);
					builder.setLoader(BookLoader.INSTANCE.getPageFactory(subData.loader));
				}
			}
			builder.addToCategory();
		}
	}

	private void loadEntries(Map<String, EntryData> entries, String entry) {
		EntryData data = fromJson(entry + ".json", EntryData.class, null);
		if (data != null) {
			entries.put(entry, data);
			for (String subEntry : data.subEntries) {
				loadEntries(entries, subEntry);
			}
		}
	}

	@Nullable
	private <T> T fromJson(String location, Class<T> classOfT, @Nullable T fallback) {
		IResource resource = getResource(location);
		if (resource == null) {
			return fallback;
		}
		try (BufferedReader reader = ResourceUtil.createReader(resource)) {
			return GSON.fromJson(reader, classOfT);
		} catch (Exception e) {
			Log.error("Failed to load resource {}: {}", location, e);
			return fallback;
		} finally {
			IOUtils.closeQuietly(resource);
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		invalidateBook();
	}

	private static class Entries {
		private final List<String> names = new LinkedList<>();
	}

	private static class EntriesDeserializer implements JsonDeserializer<Entries> {
		@Override
		public Entries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
			Entries entries = new Entries();
			parseElement(entries, json, context);
			return entries;
		}

		private void parseElement(Entries entries, JsonElement element, JsonDeserializationContext context) {
			if (element.isJsonArray()) {
				String[] array = context.deserialize(element, String[].class);
				entries.names.addAll(Arrays.asList(array));
			} else {
				JsonObject object = element.getAsJsonObject();
				for (Map.Entry<String, JsonElement> member : object.entrySet()) {
					if (ModuleHelper.isEnabled(member.getKey())) {
						parseElement(entries, member.getValue(), context);
					}
				}
			}
		}
	}
}
