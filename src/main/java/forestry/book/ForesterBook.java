package forestry.book;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.IBookCategory;
import forestry.api.book.IBookEntry;
import forestry.api.book.IForesterBook;

@OnlyIn(Dist.CLIENT)
public class ForesterBook implements IForesterBook {

	private final Map<String, IBookCategory> categoriesByName = new HashMap<>();

	@Override
	public Collection<String> getCategoryNames() {
		return categoriesByName.keySet();
	}

	@Override
	public IBookCategory addCategory(String name) {
		IBookCategory category = getCategory(name);
		if (category == null) {
			categoriesByName.put(name, category = new BookCategory(name));
		}
		return category;
	}

	void addCategories(IBookCategory[] categories) {
		for (IBookCategory category : categories) {
			categoriesByName.put(category.getName(), category);
		}
	}

	@Nullable
	@Override
	public IBookCategory getCategory(String name) {
		return categoriesByName.get(name);
	}

	@Override
	public Collection<IBookCategory> getCategories() {
		return categoriesByName.values();
	}

	@Override
	public Collection<IBookEntry> getEntries(String category) {
		IBookCategory c = getCategory(category);
		return c == null ? Collections.emptySet() : c.getEntries();
	}

	@Nullable
	@Override
	public IBookEntry getEntry(String name) {
		for (IBookCategory category : categoriesByName.values()) {
			for (IBookEntry entry : category.getEntries()) {
				if (entry.getName().equalsIgnoreCase(name)) {
					return entry;
				}
			}
		}
		return null;
	}
}
