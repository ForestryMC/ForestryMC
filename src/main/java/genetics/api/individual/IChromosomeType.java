package genetics.api.individual;

import genetics.api.alleles.IAllele;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import net.minecraft.util.text.ITextComponent;

/**
 * Interface to be implemented by the enums representing the various chromosomes.
 */
public interface IChromosomeType {
    /**
     * @return The position of a chromosome that has this type at a chromosome array.
     */
    int getIndex();

    @Deprecated
    default int ordinal() {
        return getIndex();
    }

    /**
     * @return Short identifier.
     */
    String getName();

    ITextComponent getDisplayName();

    /**
     * @return The definition that contains this type in the {@link IKaryotype}.
     * @implNote You can use {@link genetics.api.IGeneticApiInstance#getRoot(String)} to get a instance of your definition.
     */
    IIndividualRoot getRoot();

    IRootDefinition getDefinition();

    boolean isEmpty();

    boolean isValid(IAllele allele);
}
