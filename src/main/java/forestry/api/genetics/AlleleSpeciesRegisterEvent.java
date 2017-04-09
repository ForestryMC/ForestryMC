package forestry.api.genetics;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called after Forestry has registered all his species alleles of a individual.
 */
public class AlleleSpeciesRegisterEvent<A extends IAlleleSpecies> extends AlleleRegisterEvent<A> {

	private final ISpeciesRoot root;

	public AlleleSpeciesRegisterEvent(ISpeciesRoot root, Class<? extends A> alleleClass) {
		super(alleleClass);
		this.root = root;
	}

	public ISpeciesRoot getRoot() {
		return root;
	}

}
