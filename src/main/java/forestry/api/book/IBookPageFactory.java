package forestry.api.book;

import java.util.Collection;

public interface IBookPageFactory {
	Collection<IBookPage> load(IBookEntry entry);
}
