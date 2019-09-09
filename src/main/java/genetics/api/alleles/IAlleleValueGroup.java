package genetics.api.alleles;

import java.util.Collection;

public interface IAlleleValueGroup<V> extends IAlleleGroup {

	Collection<V> getAllowedValues();
}
