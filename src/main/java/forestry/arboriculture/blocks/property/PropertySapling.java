package forestry.arboriculture.blocks.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Objects;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import net.minecraft.block.properties.IProperty;

public class PropertySapling implements IProperty<IAlleleTreeSpecies> {

    private final String name;
    private final List<IAlleleTreeSpecies> trees = new ArrayList<>();

    public PropertySapling(String name){
        this.name = name;
    }

    @Override
	public String getName()
    {
        return this.name;
    }

    @Override
	public Class<IAlleleTreeSpecies> getValueClass()
    {
        return IAlleleTreeSpecies.class;
    }

    @Override
	public String toString()
    {
        return Objects.toStringHelper(this).add("name", this.name).add("clazz", IAlleleTreeSpecies.class).add("values", this.getAllowedValues()).toString();
    }

    @Override
	public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
        	PropertySapling propertyhelper = (PropertySapling)p_equals_1_;
            return this.name.equals(propertyhelper.name);
        }
        else
        {
            return false;
        }
    }

    @Override
	public int hashCode()
    {
        return 31 * IAlleleTreeSpecies.class.hashCode() + this.name.hashCode();
    }

	@Override
	public Collection<IAlleleTreeSpecies> getAllowedValues() {
		List<IAlleleTreeSpecies> trees = new ArrayList<>();
		for(IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()){
			if(allele instanceof IAlleleTreeSpecies)
				trees.add((IAlleleTreeSpecies) allele);
		}
		return trees;
	}

	@Override
	public String getName(IAlleleTreeSpecies value) {
		return value.getName();
	}
	
}
