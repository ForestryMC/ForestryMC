/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.blocks.propertys;

import java.util.ArrayList;
import java.util.List;

import forestry.api.genetics.IAllele;
import net.minecraft.block.properties.IProperty;

public abstract class PropertyAllele<A extends IAllele & Comparable<A>> implements IProperty<A> {

    protected final String name;
    protected final List<A> alleles = new ArrayList<A>();

    public PropertyAllele(String name){
        this.name = name;
    }

    @Override
	public String getName()
    {
        return this.name;
    }

    @Override
	public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        else if (object != null && this.getClass() == object.getClass())
        {
        	PropertyAllele propertyhelper = (PropertyAllele)object;
            return this.name.equals(propertyhelper.name);
        }
        else
        {
            return false;
        }
    }
	
}
