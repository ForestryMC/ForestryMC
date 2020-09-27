package forestry.api.genetics.alyzer;

import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismType;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**
 * Helper for creating easily a multiline multicolumn gui like the forestry alyzer. Can be used to create custom gui
 * renderings for your alleles in the alyzer.
 *
 * @see IAlleleDisplayHandler
 */
public interface IAlyzerHelper {
    void addWidget(Widget widget);

    IIndividual getIndividual();

    IOrganism<? extends IIndividual> getOrganism();

    default IGenome getGenome() {
        return getIndividual().getGenome();
    }

    IOrganismType getOrganismType();

    default void drawChromosomeRow(String text, IChromosomeType type) {
        drawChromosomeRow(text, type, false);
    }

    void drawChromosomeRow(String key, IChromosomeType type, boolean translate);

    void newLine();

    void nextColumn();

    int getLineY();

    int getColumnX();

    default void drawLine(String key, int xOffset) {
        drawLine(key, xOffset, false);
    }

    void drawLine(String key, boolean translate);

    void drawLine(String key, int xOffset, boolean translate);

    default void drawFertilityInfo(IAlleleValue<Integer> allele) {
        drawFertilityInfo(allele.getValue(), getColorCoding(allele.isDominant()), 0);
    }

    int getColorCoding(boolean dominant);

    void drawFertilityInfo(int value, int textColor, int xOffset);

    /**
     * Retrieves a pre created stack that should be used to display the given species in a gui. The idea behind this is
     * to only need to create one stack for each species.
     */
    ItemStack getDisplayStack(IAlleleSpecies species, IOrganismType type);

    ItemStack getDisplayStack(IAlleleSpecies species);

    void drawSpeciesRow(
            String caption,
            IChromosomeAllele<? extends IAlleleForestrySpecies> speciesType,
            ITextComponent primaryName,
            ITextComponent secondaryName
    );

    class Column {
        public static final int FIRST = 12;
        public static final int SECOND = 90;
        public static final int THIRD = 155;
    }
}
