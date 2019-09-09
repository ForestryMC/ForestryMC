package genetics.api.root;

import java.util.Collection;

public interface IGeneticListenerRegistry {

	void add(String uid, IGeneticListener listener);

	void add(String uid, IGeneticListener... listeners);

	void add(String uid, Collection<IGeneticListener> listeners);
}
