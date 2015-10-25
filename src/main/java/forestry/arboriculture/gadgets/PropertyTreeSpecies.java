package forestry.arboriculture.gadgets;

import java.util.Collection;

import net.minecraft.block.properties.PropertyHelper;

public class PropertyTreeSpecies extends PropertyHelper {

	protected PropertyTreeSpecies(String name, Class valueClass) {
		super(name, valueClass);
	}

	@Override
	public Collection getAllowedValues() {
		return null;
	}

	@Override
	public String getName(Comparable value) {
		return null;
	}

}
