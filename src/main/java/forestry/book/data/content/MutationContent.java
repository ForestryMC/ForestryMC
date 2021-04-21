package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.book.gui.elements.MutationElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ElementGroup;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;
import genetics.utils.AlleleUtils;

/**
 * A book content that displays one or more mutations.
 */
@OnlyIn(Dist.CLIENT)
public class MutationContent extends BookContent {
	public String species = "";

	@Nullable
	@Override
	public Class getDataClass() {
		return null;
	}

	@Override
	public boolean addElements(ElementGroup page, GuiElementFactory factory, @Nullable BookContent previous, @Nullable GuiElement previousElement, int pageHeight) {
		IAllele allele = AlleleUtils.getAlleleOrNull(species);
		if (!(allele instanceof IAlleleForestrySpecies)) {
			return false;
		}
		IAlleleForestrySpecies s = (IAlleleForestrySpecies) allele;
		IForestrySpeciesRoot<IIndividual> root = (IForestrySpeciesRoot<IIndividual>) s.getRoot();
		IMutationContainer<IIndividual, ? extends IMutation> container = root.getComponent(ComponentKeys.MUTATIONS);
		page.add(new MutationElement(0, 0, container.getResultantMutations(s).toArray(new IMutation[0])));
		return true;
	}
}
