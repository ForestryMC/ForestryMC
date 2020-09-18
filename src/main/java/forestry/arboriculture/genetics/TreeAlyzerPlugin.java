/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.genetics;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.config.Config;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.StringUtil;
import genetics.api.GeneticHelper;
import genetics.api.individual.IGenome;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

public class TreeAlyzerPlugin implements IAlyzerPlugin {
    public static final TreeAlyzerPlugin INSTANCE = new TreeAlyzerPlugin();

    protected final Map<ResourceLocation, ItemStack> iconStacks = new HashMap<>();

    private TreeAlyzerPlugin() {
        NonNullList<ItemStack> treeList = NonNullList.create();
        ArboricultureItems.SAPLING.item().addCreativeItems(treeList, false);
        for (ItemStack treeStack : treeList) {
            IOrganism<?> organism = GeneticHelper.getOrganism(treeStack);
            if (organism.isEmpty()) {
                continue;
            }
            IAlleleTreeSpecies species = organism.getAllele(TreeChromosomes.SPECIES, true);
            iconStacks.put(species.getRegistryName(), treeStack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage1(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<ITree> optional = TreeManager.treeRoot.create(itemStack);
            if (!optional.isPresent()) {
                return;
            }

            ITree tree = optional.get();
            Optional<IOrganismType> typeOptional = TreeManager.treeRoot.getTypes().getType(itemStack);
            if (!typeOptional.isPresent()) {
                return;
            }

            IOrganismType type = typeOptional.get();
            IGenome genome = tree.getGenome();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.active"), GuiAlyzer.COLUMN_1);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.inactive"), GuiAlyzer.COLUMN_2);

            textLayout.newLine();
            textLayout.newLine();

            {
                String customPrimaryTreeKey = "trees.custom.treealyzer." + type.getName() + "."
                        + tree.getGenome().getPrimary().getLocalisationKey().replace("trees.species.", "");
                String customSecondaryTreeKey = "trees.custom.treealyzer." + type.getName() + "."
                        + tree.getGenome().getSecondary().getLocalisationKey().replace("trees.species.", "");

                guiAlyzer.drawSpeciesRow(
                        transform,
                        new TranslationTextComponent("for.gui.species"),
                        tree,
                        TreeChromosomes.SPECIES,
                        I18n.hasKey(customPrimaryTreeKey) ? new TranslationTextComponent(customPrimaryTreeKey) : null,
                        I18n.hasKey(customSecondaryTreeKey) ? new TranslationTextComponent(customSecondaryTreeKey) : null
                );
                textLayout.newLine();
            }

            guiAlyzer.drawChromosomeRow(transform, new TranslationTextComponent("for.gui.saplings"), tree, TreeChromosomes.FERTILITY);
            textLayout.newLineCompressed();
            guiAlyzer.drawChromosomeRow(transform, new TranslationTextComponent("for.gui.maturity"), tree, TreeChromosomes.MATURATION);
            textLayout.newLineCompressed();
            guiAlyzer.drawChromosomeRow(transform, new TranslationTextComponent("for.gui.height"), tree, TreeChromosomes.HEIGHT);
            textLayout.newLineCompressed();

            float activeGirth = genome.getActiveValue(TreeChromosomes.GIRTH);
            float inactiveGirth = genome.getInactiveValue(TreeChromosomes.GIRTH);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.girth"), GuiAlyzer.COLUMN_0);
            guiAlyzer.drawLine(
                    transform,
                    ITextComponent.func_241827_a_(String.format("%sx%s", activeGirth, activeGirth)),
                    GuiAlyzer.COLUMN_1,
                    tree,
                    TreeChromosomes.GIRTH,
                    false);
            guiAlyzer.drawLine(
                    transform,
                    ITextComponent.func_241827_a_(String.format("%sx%s", inactiveGirth, inactiveGirth)),
                    GuiAlyzer.COLUMN_2,
                    tree,
                    TreeChromosomes.GIRTH,
                    true);

            textLayout.newLineCompressed();

            guiAlyzer.drawChromosomeRow(transform, new TranslationTextComponent("for.gui.yield"), tree, TreeChromosomes.YIELD);
            textLayout.newLineCompressed();
            guiAlyzer.drawChromosomeRow(transform, new TranslationTextComponent("for.gui.sappiness"), tree, TreeChromosomes.SAPPINESS);
            textLayout.newLineCompressed();

            guiAlyzer.drawChromosomeRow(transform, new TranslationTextComponent("for.gui.effect"), tree, TreeChromosomes.EFFECT);

            textLayout.endPage();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage2(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<ITree> optional = TreeManager.treeRoot.create(itemStack);
            if (!optional.isPresent()) {
                return;
            }
            ITree tree = optional.get();
            IGenome genome = tree.getGenome();
            IAlleleTreeSpecies primary = genome.getActiveAllele(TreeChromosomes.SPECIES);
            IAlleleTreeSpecies secondary = genome.getActiveAllele(TreeChromosomes.SPECIES);
            IFruitProvider primaryFruit = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).getProvider();
            IFruitProvider secondaryFruit = tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS).getProvider();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            int speciesDominance0 = guiAlyzer.getColorCoding(primary.isDominant());
            int speciesDominance1 = guiAlyzer.getColorCoding(genome.getSecondary().isDominant());

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.active"), GuiAlyzer.COLUMN_1);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.inactive"), GuiAlyzer.COLUMN_2);

            textLayout.newLine();
            textLayout.newLine();

            ITextProperties yes = new TranslationTextComponent("for.yes");
            ITextProperties no = new TranslationTextComponent("for.no");

            ITextProperties fireproofActive = StringUtil.readableBoolean(genome.getActiveValue(TreeChromosomes.FIREPROOF), yes, no);
            ITextProperties fireproofInactive = StringUtil.readableBoolean(genome.getInactiveValue(TreeChromosomes.FIREPROOF), yes, no);

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.fireproof"),
                    fireproofActive,
                    fireproofInactive,
                    tree,
                    TreeChromosomes.FIREPROOF
            );

            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.native"), GuiAlyzer.COLUMN_0);
            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui." + primary.getPlantType().getName().toLowerCase(Locale.ENGLISH)),
                    GuiAlyzer.COLUMN_1,
                    speciesDominance0
            );
            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui." + secondary.getPlantType().getName().toLowerCase(Locale.ENGLISH)),
                    GuiAlyzer.COLUMN_2,
                    speciesDominance1
            );

            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.supports"), GuiAlyzer.COLUMN_0);
            List<IFruitFamily> families0 = new ArrayList<>(primary.getSuitableFruit());
            List<IFruitFamily> families1 = new ArrayList<>(secondary.getSuitableFruit());

            int max = Math.max(families0.size(), families1.size());
            for (int i = 0; i < max; i++) {
                if (i > 0) {
                    textLayout.newLineCompressed();
                }

                if (families0.size() > i) {
                    textLayout.drawLine(transform, families0.get(i).getName(), GuiAlyzer.COLUMN_1, speciesDominance0);
                }

                if (families1.size() > i) {
                    textLayout.drawLine(transform, families1.get(i).getName(), GuiAlyzer.COLUMN_2, speciesDominance1);
                }

            }

            textLayout.newLine();

            // FRUITS
            int fruitDominance0 = guiAlyzer.getColorCoding(tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).isDominant());
            int fruitDominance1 = guiAlyzer.getColorCoding(tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS).isDominant());

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.fruits"), GuiAlyzer.COLUMN_0);
            String strike = "";
            IAlleleFruit fruit0 = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS);
            if (!tree.canBearFruit() && fruit0 != AlleleFruits.fruitNone) {
                strike = TextFormatting.STRIKETHROUGH.toString();
            }
            textLayout.drawLine(
                    transform,
                    ITextComponent.func_241827_a_(strike + fruit0.getProvider().getDescription().getString()),
                    GuiAlyzer.COLUMN_1,
                    fruitDominance0
            );

            strike = "";
            IAlleleFruit fruit1 = tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS);
            if (!secondary.getSuitableFruit().contains(fruit1.getProvider().getFamily()) && fruit1 != AlleleFruits.fruitNone) {
                strike = TextFormatting.STRIKETHROUGH.toString();
            }
            textLayout.drawLine(
                    transform,
                    ITextComponent.func_241827_a_(strike + fruit1.getProvider().getDescription().getString()),
                    GuiAlyzer.COLUMN_2,
                    fruitDominance1
            );

            textLayout.newLine();

            // FAMILY
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.family"), GuiAlyzer.COLUMN_0);

            if (primaryFruit.getFamily() != null && !primaryFruit.getFamily().getUID().equals(EnumFruitFamily.NONE.getUID())) {
                textLayout.drawLine(transform, primaryFruit.getFamily().getName(), GuiAlyzer.COLUMN_1, fruitDominance0);
            }

            if (secondaryFruit.getFamily() != null && !secondaryFruit.getFamily().getUID().equals(EnumFruitFamily.NONE.getUID())) {
                textLayout.drawLine(transform, secondaryFruit.getFamily().getName(), GuiAlyzer.COLUMN_2, fruitDominance1);
            }

            textLayout.endPage();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage3(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<ITree> optional = TreeManager.treeRoot.create(itemStack);
            if (!optional.isPresent()) {
                return;
            }
            ITree tree = optional.get();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
            WidgetManager widgetManager = guiAlyzer.getWidgetManager();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.beealyzer.produce").appendString(":"), GuiAlyzer.COLUMN_0);
            textLayout.newLine();

            int x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : tree.getProducts().getPossibleStacks()) {
                widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));
                x += 18;
                if (x > 148) {
                    x = GuiAlyzer.COLUMN_0;
                    textLayout.newLine();
                }
            }

            textLayout.newLine();
            textLayout.newLine();
            textLayout.newLine();
            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.beealyzer.specialty").appendString(":"), GuiAlyzer.COLUMN_0);
            textLayout.newLine();

            x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : tree.getSpecialties().getPossibleStacks()) {
                Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(
                        stack,
                        guiAlyzer.getGuiLeft() + x,
                        guiAlyzer.getGuiTop() + textLayout.getLineY()
                );
                x += 18;
                if (x > 148) {
                    x = GuiAlyzer.COLUMN_0;
                    textLayout.newLine();
                }
            }

            textLayout.endPage();
        }
    }

    @Override
    public Map<ResourceLocation, ItemStack> getIconStacks() {
        return iconStacks;
    }

    @Override
    public List<String> getHints() {
        return Config.hints.get("treealyzer");
    }
}
