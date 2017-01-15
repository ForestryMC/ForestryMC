/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraftforge.fluids.FluidStack;

public enum EnumContainerType {
	GLASS, JAR, CAN, CAPSULE, REFRACTORY;

	private List<IFluidContainerPermission> permissions = new ArrayList<>();
	private final String name;

	EnumContainerType() {
		this.name = name().toLowerCase(Locale.ENGLISH).replace('_', '.');
	}

	public String getName() {
		return name;
	}
	
	public void add(IFluidContainerPermission permission){
		permissions.add(permission);
	}
	
	public boolean contentsAllowed(FluidStack fluidStack){
		for(IFluidContainerPermission permission : permissions){
			if(!permission.contentsAllowed(fluidStack)){
				return false;
			}
		}
		return true;
	}
}
