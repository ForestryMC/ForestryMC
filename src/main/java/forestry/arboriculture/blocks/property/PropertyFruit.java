package forestry.arboriculture.blocks.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Objects;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.blocks.propertys.PropertyAllele;

public class PropertyFruit extends PropertyAllele<IAlleleFruit> {

    public PropertyFruit(String name){
    	super(name);
    }

    @Override
	public Class<IAlleleFruit> getValueClass()
    {
        return IAlleleFruit.class;
    }

    @Override
	public String toString()
    {
        return Objects.toStringHelper(this).add("name", this.name).add("clazz", IAlleleFruit.class).add("values", this.getAllowedValues()).toString();
    }

    @Override
	public int hashCode()
    {
        return 31 * IAlleleTreeSpecies.class.hashCode() + this.name.hashCode();
    }

	@Override
	public Collection<IAlleleFruit> getAllowedValues() {
		List<IAlleleFruit> trees = new ArrayList<>();
		for(IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()){
			if(allele instanceof IAlleleFruit)
				if(((IAlleleFruit) allele).getModelName() != null)
					trees.add((IAlleleFruit) allele);
		}
		return trees;
	}

	@Override
	public String getName(IAlleleFruit value) {
		return value.getModelName();
	}

}
