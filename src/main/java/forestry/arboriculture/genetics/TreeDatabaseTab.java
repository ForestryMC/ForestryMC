package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Style;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.gui.GuiConstants;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.DatabaseElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.utils.Translator;

import genetics.api.alleles.IAlleleValue;

@OnlyIn(Dist.CLIENT)
public class TreeDatabaseTab implements IDatabaseTab<ITree> {
	private final DatabaseMode mode;

	TreeDatabaseTab(DatabaseMode mode) {
		this.mode = mode;
	}

	@Override
	public DatabaseMode getMode() {
		return mode;
	}

	@Override
	public void createElements(DatabaseElement container, ITree tree, ItemStack itemStack) {
		IAlleleTreeSpecies primarySpecies = tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES);
		IAlleleTreeSpecies species = mode == DatabaseMode.ACTIVE ? primarySpecies : tree.getGenome().getInactiveAllele(TreeChromosomes.SPECIES);
		Style speciesStyle = GuiElementFactory.INSTANCE.getStateStyle(species.isDominant());

		container.translated("for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") + "_species.name").setStyle(GuiElementFactory.INSTANCE.databaseTitle).setAlign(Alignment.TOP_CENTER);

		container.addLine(Translator.translateToLocal("for.gui.species"), TreeChromosomes.SPECIES);

		container.addLine(Translator.translateToLocal("for.gui.saplings"), TreeChromosomes.FERTILITY);
		container.addLine(Translator.translateToLocal("for.gui.maturity"), TreeChromosomes.MATURATION);
		container.addLine(Translator.translateToLocal("for.gui.height"), TreeChromosomes.HEIGHT);

		container.addLine(Translator.translateToLocal("for.gui.girth"), (IAlleleValue<Integer> girth, Boolean active) -> String.format("%sx%s", girth.getValue(), girth.getValue()), TreeChromosomes.GIRTH);

		container.addLine(Translator.translateToLocal("for.gui.yield"), TreeChromosomes.YIELD);
		container.addLine(Translator.translateToLocal("for.gui.sappiness"), TreeChromosomes.SAPPINESS);

		container.addLine(Translator.translateToLocal("for.gui.effect"), TreeChromosomes.EFFECT);

		container.addLine(Translator.translateToLocal("for.gui.native"), Translator.translateToLocal("for.gui." + primarySpecies.getPlantType().toString().toLowerCase(Locale.ENGLISH)), species.isDominant());

		container.label(Translator.translateToLocal("for.gui.supports"), Alignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
		List<IFruitFamily> families = new ArrayList<>(primarySpecies.getSuitableFruit());

		for (IFruitFamily fruitFamily : families) {
			container.label(fruitFamily.getName().getString(), Alignment.TOP_CENTER, speciesStyle);
		}

		IAlleleFruit fruit = mode == DatabaseMode.ACTIVE ? tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS) : tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS);
		Style textStyle = GuiElementFactory.INSTANCE.getStateStyle(tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).isDominant());

		container.translated("for.gui.fruits").setStyle(GuiConstants.UNDERLINED_STYLE).setAlign(Alignment.TOP_CENTER);
		Style fruitStyle = textStyle;
		String strike = "";
		if (!species.getSuitableFruit().contains(fruit.getProvider().getFamily()) && fruit != AlleleFruits.fruitNone) {
			fruitStyle = fruitStyle.setStrikethrough(true);
		}
		container.label(fruit.getProvider().getDescription()).setStyle(fruitStyle).setAlign(Alignment.TOP_CENTER);

		IFruitFamily family = fruit.getProvider().getFamily();

		if (family != null && !family.getUID().equals(EnumFruitFamily.NONE.getUID())) {
			container.label(Translator.translateToLocal("for.gui.family"), Alignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
			container.label(family.getName().getString(), Alignment.TOP_CENTER, textStyle);
		}

	}

	@Override
	public ItemStack getIconStack() {
		return TreeDefinition.Cherry.getMemberStack(mode == DatabaseMode.ACTIVE ? EnumGermlingType.SAPLING : EnumGermlingType.POLLEN);
	}
}
