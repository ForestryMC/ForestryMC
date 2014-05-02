/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * @author Alex Binnie
 * 
 *         Handler for events that occur in IAlleleRegistry, such as registering alleles, branches etc. Useful for handling plugin specific behavior (i.e.
 *         creating a list of all bee species etc.)
 * 
 */
public interface IAlleleHandler {

	/**
	 * Called when an allele is registered with {@link IAlleleRegistry}.
	 * 
	 * @param allele
	 *            Allele which was registered.
	 */
	public void onRegisterAllele(IAllele allele);

	/**
	 * Called when a classification is registered with {@link IAlleleRegistry}.
	 * 
	 * @param classification
	 *            Classification which was registered.
	 */
	public void onRegisterClassification(IClassification classification);

	/**
	 * Called when a fruit family is registered with {@link IAlleleRegistry}.
	 * 
	 * @param family
	 *            Fruit family which was registered.
	 */
	public void onRegisterFruitFamily(IFruitFamily family);

}
