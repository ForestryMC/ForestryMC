/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called after Forestry has registered all his alleles of an specific type. Like IAlleleFruit or IAlleleSpecies
 * @since 5.3.3
 */
public class AlleleRegisterEvent<A extends IAllele> extends Event {

	private final Class<? extends A> alleleClass;

	public AlleleRegisterEvent(Class<? extends A> alleleClass) {
		this.alleleClass = alleleClass;
	}
	
	public Class<? extends A> getAlleleClass() {
		return alleleClass;
	}

}
