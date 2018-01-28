package forestry.api.book;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IForesterBook {

	IBookCategory addCategory(String name);

	@Nullable
	IBookCategory getCategory(String name);

	Collection<IBookCategory> getCategories();

	Collection<String> getCategoryNames();

	Collection<IBookEntry> getEntries(String category);

	@Nullable
	IBookEntry getEntry(String name);
}
