package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.IFruitFamily;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IElementGenetic;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.api.core.Translator;

@SideOnly(Side.CLIENT)
public class TreeDatabaseTab implements IDatabaseTab<ITree> {
	private final boolean active;

	TreeDatabaseTab(boolean active) {
		this.active = active;
	}

	@Override
	public void createElements(IElementGenetic container, ITree tree, ItemStack itemStack) {
		IAlleleTreeSpecies primarySpecies = tree.getGenome().getPrimary();
		IAlleleTreeSpecies species = active ? primarySpecies : tree.getGenome().getSecondary();
		int speciesColor = GuiElementFactory.INSTANCE.getColorCoding(species.isDominant());

		container.text(Translator.translateToLocal("for.gui.database.tab." + (active ? "active" : "inactive") + "_species.name"), GuiElementAlignment.TOP_CENTER, 0xcfb53b);

		container.addAlleleRow(Translator.translateToLocal("for.gui.species"), tree, EnumTreeChromosome.SPECIES, active);

		container.addAlleleRow(Translator.translateToLocal("for.gui.saplings"), tree, EnumTreeChromosome.FERTILITY, active);
		container.addAlleleRow(Translator.translateToLocal("for.gui.maturity"), tree, EnumTreeChromosome.MATURATION, active);
		container.addAlleleRow(Translator.translateToLocal("for.gui.height"), tree, EnumTreeChromosome.HEIGHT, active);

		IAlleleInteger girth = (IAlleleInteger) (active ? tree.getGenome().getActiveAllele(EnumTreeChromosome.GIRTH) : tree.getGenome().getInactiveAllele(EnumTreeChromosome.GIRTH));
		container.addRow(Translator.translateToLocal("for.gui.girth"), String.format("%sx%s", girth.getValue(), girth.getValue()), girth.isDominant());

		container.addAlleleRow(Translator.translateToLocal("for.gui.yield"), tree, EnumTreeChromosome.YIELD, active);
		container.addAlleleRow(Translator.translateToLocal("for.gui.sappiness"), tree, EnumTreeChromosome.SAPPINESS, active);

		container.addAlleleRow(Translator.translateToLocal("for.gui.effect"), tree, EnumTreeChromosome.EFFECT, active);

		container.addRow(Translator.translateToLocal("for.gui.native"), Translator.translateToLocal("for.gui." + tree.getGenome().getPrimary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), species.isDominant());

		container.text(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.supports"), GuiElementAlignment.TOP_CENTER);
		List<IFruitFamily> families = new ArrayList<>(tree.getGenome().getPrimary().getSuitableFruit());

		for (IFruitFamily fruitFamily : families) {
			container.text(fruitFamily.getName(), GuiElementAlignment.TOP_CENTER, speciesColor);
		}

		IAlleleFruit fruit = (IAlleleFruit) (active ? tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS) : tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS));
		int colorCoding = GuiElementFactory.INSTANCE.getColorCoding(tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS).isDominant());

		container.text(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.fruits"), GuiElementAlignment.TOP_CENTER);
		String strike = "";
		if (!species.getSuitableFruit().contains(fruit.getProvider().getFamily()) && fruit != AlleleFruits.fruitNone) {
			strike = TextFormatting.STRIKETHROUGH.toString();
		}
		container.text(strike + fruit.getProvider().getDescription(), GuiElementAlignment.TOP_CENTER, colorCoding);

		IFruitFamily family = fruit.getProvider().getFamily();

		if (family != null && !family.getUID().equals(EnumFruitFamily.NONE.getUID())) {
			container.text(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.family"), GuiElementAlignment.TOP_CENTER);
			container.text(family.getName(), GuiElementAlignment.TOP_CENTER, colorCoding);
		}

	}

	@Override
	public ItemStack getIconStack() {
		return TreeDefinition.Cherry.getMemberStack(active ? EnumGermlingType.SAPLING : EnumGermlingType.POLLEN);
	}
}
