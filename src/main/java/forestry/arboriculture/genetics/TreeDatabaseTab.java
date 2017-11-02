package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElementHelper;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class TreeDatabaseTab implements IDatabaseTab<ITree> {
	private final EnumDatabaseTab tab;

	TreeDatabaseTab(EnumDatabaseTab tab) {
		this.tab = tab;
	}

	@Override
	public void createElements(IGuiElementHelper elementHelper, ITree tree, ItemStack itemStack) {
		IAlleleTreeSpecies primarySpecies = tree.getGenome().getPrimary();
		boolean active = tab == EnumDatabaseTab.ACTIVE_SPECIES;
		IAlleleTreeSpecies species = active ? primarySpecies : tree.getGenome().getSecondary();
		int speciesColor = elementHelper.factory().getColorCoding(species.isDominant());

		elementHelper.addText(Translator.translateToLocal("for.gui.database.tab." + tab.name().toLowerCase() + ".name"), GuiElementAlignment.CENTER, 0xcfb53b);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.species"), tree, EnumTreeChromosome.SPECIES, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.saplings"), tree, EnumTreeChromosome.FERTILITY, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.maturity"), tree, EnumTreeChromosome.MATURATION, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.height"), tree, EnumTreeChromosome.HEIGHT, active);

		IAlleleInteger girth = (IAlleleInteger) (active ? tree.getGenome().getActiveAllele(EnumTreeChromosome.GIRTH) : tree.getGenome().getInactiveAllele(EnumTreeChromosome.GIRTH));
		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.girth"), GuiElementAlignment.CENTER);
		elementHelper.addText(String.format("%sx%s", girth.getValue(), girth.getValue()), GuiElementAlignment.CENTER, elementHelper.factory().getColorCoding(girth.isDominant()));

		elementHelper.addAllele(Translator.translateToLocal("for.gui.yield"), tree, EnumTreeChromosome.YIELD, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.sappiness"), tree, EnumTreeChromosome.SAPPINESS, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.effect"), tree, EnumTreeChromosome.EFFECT, active);

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.native"), GuiElementAlignment.CENTER);
		elementHelper.addText(Translator.translateToLocal("for.gui." + tree.getGenome().getPrimary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), GuiElementAlignment.CENTER, speciesColor);

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.supports"), GuiElementAlignment.CENTER);
		List<IFruitFamily> families = new ArrayList<>(tree.getGenome().getPrimary().getSuitableFruit());

		for (IFruitFamily fruitFamily : families) {
			elementHelper.addText(fruitFamily.getName(), GuiElementAlignment.CENTER, speciesColor);
		}

		IAlleleFruit fruit = (IAlleleFruit) (active ? tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS) : tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS));
		int colorCoding = elementHelper.factory().getColorCoding(tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS).isDominant());

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.fruits"), GuiElementAlignment.CENTER);
		String strike = "";
		if (!species.getSuitableFruit().contains(fruit.getProvider().getFamily()) && fruit != AlleleFruits.fruitNone) {
			strike = TextFormatting.STRIKETHROUGH.toString();
		}
		elementHelper.addText(strike + fruit.getProvider().getDescription(), GuiElementAlignment.CENTER, colorCoding);

		IFruitFamily family = fruit.getProvider().getFamily();

		if (family != null && !family.getUID().equals(EnumFruitFamily.NONE.getUID())) {
			elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.family"), GuiElementAlignment.CENTER);
			elementHelper.addText(family.getName(), GuiElementAlignment.CENTER, colorCoding);
		}

	}

	@Override
	public EnumDatabaseTab getTab() {
		return tab;
	}
}
