package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.BookContent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.gui.elements.MutationElement;

/**
 * A book content that displays one or more mutations.
 */
@SideOnly(Side.CLIENT)
public class MutationContent extends BookContent {
	public String species = "";

	@Nullable
	@Override
	public Class getDataClass() {
		return null;
	}

	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
		IAllele allele = AlleleManager.alleleRegistry.getAllele(species);
		if (!(allele instanceof IAlleleSpecies)) {
			return false;
		}
		IAlleleSpecies s = (IAlleleSpecies) allele;
		ISpeciesRoot root = s.getRoot();
		page.add(new MutationElement(0, 0, root.getResultantMutations(s).toArray(new IMutation[0])));
		return true;
	}
}
