package forestry.arboriculture.genetics;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.genetics.*;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.lib.GuiConstants;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import genetics.api.alleles.IAlleleValue;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public void createElements(IDatabaseElement container, ITree tree, ItemStack itemStack) {
        IAlleleTreeSpecies primarySpecies = tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES);
        IAlleleTreeSpecies species = mode == DatabaseMode.ACTIVE ? primarySpecies : tree.getGenome()
                                                                                        .getInactiveAllele(
                                                                                                TreeChromosomes.SPECIES);
        Style speciesStyle = GuiElementFactory.INSTANCE.getStateStyle(species.isDominant());

        container.translated(
                "for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") + "_species.name")
                 .setStyle(GuiElementFactory.INSTANCE.databaseTitle)
                 .setAlign(GuiElementAlignment.TOP_CENTER);

        container.addLine(new TranslationTextComponent("for.gui.species"), TreeChromosomes.SPECIES);

        container.addLine(new TranslationTextComponent("for.gui.saplings"), TreeChromosomes.FERTILITY);
        container.addLine(new TranslationTextComponent("for.gui.maturity"), TreeChromosomes.MATURATION);
        container.addLine(new TranslationTextComponent("for.gui.height"), TreeChromosomes.HEIGHT);

        container.addLine(
                new TranslationTextComponent("for.gui.girth"),
                (IAlleleValue<Integer> girth, Boolean active) -> new StringTextComponent(String.format(
                        "%sx%s",
                        girth.getValue(),
                        girth.getValue()
                )),
                TreeChromosomes.GIRTH
        );

        container.addLine(new TranslationTextComponent("for.gui.yield"), TreeChromosomes.YIELD);
        container.addLine(new TranslationTextComponent("for.gui.sappiness"), TreeChromosomes.SAPPINESS);

        container.addLine(new TranslationTextComponent("for.gui.effect"), TreeChromosomes.EFFECT);

        container.addLine(
                new TranslationTextComponent("for.gui.native"),
                new TranslationTextComponent(
                        "for.gui." + primarySpecies.getPlantType().getName().toLowerCase(Locale.ENGLISH)
                ),
                species.isDominant()
        );

        container.label(
                new TranslationTextComponent("for.gui.supports"),
                GuiElementAlignment.TOP_CENTER,
                GuiConstants.UNDERLINED_STYLE
        );
        List<IFruitFamily> families = new ArrayList<>(primarySpecies.getSuitableFruit());

        for (IFruitFamily fruitFamily : families) {
            container.label(fruitFamily.getName(), GuiElementAlignment.TOP_CENTER, speciesStyle);
        }

        IAlleleFruit fruit = mode == DatabaseMode.ACTIVE
                             ? tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS)
                             : tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS);

        Style textStyle = GuiElementFactory.INSTANCE.getStateStyle(
                tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).isDominant()
        );

        container.translated("for.gui.fruits")
                 .setStyle(GuiConstants.UNDERLINED_STYLE)
                 .setAlign(GuiElementAlignment.TOP_CENTER);
        Style fruitStyle = textStyle;
        String strike = "";
        if (!species.getSuitableFruit().contains(fruit.getProvider().getFamily()) && fruit != AlleleFruits.fruitNone) {
            fruitStyle = fruitStyle.setStrikethrough(true);
        }
        container.label(fruit.getProvider().getDescription())
                 .setStyle(fruitStyle)
                 .setAlign(GuiElementAlignment.TOP_CENTER);

        IFruitFamily family = fruit.getProvider().getFamily();

        if (family != null && !family.getUID().equals(EnumFruitFamily.NONE.getUID())) {
            container.label(
                    new TranslationTextComponent("for.gui.family"),
                    GuiElementAlignment.TOP_CENTER,
                    GuiConstants.UNDERLINED_STYLE
            );
            container.label(family.getName(), GuiElementAlignment.TOP_CENTER, textStyle);
        }
    }

    @Override
    public ItemStack getIconStack() {
        return TreeDefinition.Cherry.getMemberStack(
                mode == DatabaseMode.ACTIVE ? EnumGermlingType.SAPLING : EnumGermlingType.POLLEN);
    }
}
