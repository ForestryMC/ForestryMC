package forestry.core.blocks.property;

import java.util.ArrayList;
import java.util.List;

import forestry.api.genetics.IAllele;
import net.minecraft.block.properties.IProperty;

public abstract class PropertyAllele<A extends IAllele & Comparable<A>> implements IProperty<A> {

    protected final String name;
    protected final List<A> trees = new ArrayList<A>();

    public PropertyAllele(String name){
        this.name = name;
    }

    @Override
	public String getName()
    {
        return this.name;
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
        	PropertyAllele propertyhelper = (PropertyAllele)p_equals_1_;
            return this.name.equals(propertyhelper.name);
        }
        else
        {
            return false;
        }
    }
	
}
