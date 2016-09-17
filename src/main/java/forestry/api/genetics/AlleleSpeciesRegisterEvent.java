package forestry.api.genetics;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called after Forestry has registered all his species alleles of a individual.
 */
public class AlleleSpeciesRegisterEvent extends Event {
	
	private final ISpeciesRoot root;
	
	public AlleleSpeciesRegisterEvent(ISpeciesRoot root) {
		this.root = root;
	}
	
	public ISpeciesRoot getRoot() {
		return root;
	}

}
