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
package forestry.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.render.ColourProperties;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.alleles.IAlleleValue;
import genetics.api.classification.IClassification;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKeys;
import genetics.utils.AlleleUtils;
import genetics.utils.RootUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.*;

public class GuiAlyzer extends GuiForestry<ContainerAlyzer> {

    public static final int COLUMN_0 = 12;
    public static final int COLUMN_1 = 90;
    public static final int COLUMN_2 = 155;

    private final ItemInventoryAlyzer itemInventory;

    public GuiAlyzer(ContainerAlyzer container, PlayerInventory playerInv, ITextComponent name) {
        super(
                Constants.TEXTURE_PATH_GUI + "/portablealyzer.png",
                container,
                playerInv,
                new StringTextComponent("GUI_ALYZER_TEST_TITLE")
        );

        this.itemInventory = container.inventory;
        this.xSize = 246;
        this.ySize = 238;
    }

    public final int getColorCoding(boolean dominant) {
        if (dominant) {
            return ColourProperties.INSTANCE.get("gui.beealyzer.dominant");
        } else {
            return ColourProperties.INSTANCE.get("gui.beealyzer.recessive");
        }
    }

    public final void drawLine(
            MatrixStack transform,
            ITextProperties text,
            int x,
            IIndividual individual,
            IChromosomeType chromosome,
            boolean inactive
    ) {
        if (!inactive) {
            textLayout.drawLine(
                    transform,
                    text,
                    x,
                    getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant())
            );
        } else {
            textLayout.drawLine(
                    transform,
                    text,
                    x,
                    getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant())
            );
        }
    }

    public final void drawSplitLine(
            ITextProperties text,
            int x,
            int maxWidth,
            IIndividual individual,
            IChromosomeType chromosome,
            boolean inactive
    ) {
        if (!inactive) {
            textLayout.drawSplitLine(
                    text,
                    x,
                    maxWidth,
                    getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant())
            );
        } else {
            textLayout.drawSplitLine(
                    text,
                    x,
                    maxWidth,
                    getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant())
            );
        }
    }

    public final void drawRow(
            MatrixStack transform,
            ITextProperties text0,
            ITextProperties text1,
            ITextProperties text2,
            IIndividual individual,
            IChromosomeType chromosome
    ) {
        textLayout.drawRow(
                transform,
                text0,
                text1,
                text2,
                ColourProperties.INSTANCE.get("gui.screen"),
                getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()),
                getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant())
        );
    }

    public final void drawChromosomeRow(
            MatrixStack transform,
            ITextProperties chromosomeName,
            IIndividual individual,
            IChromosomeType chromosome
    ) {
        IAllele active = individual.getGenome().getActiveAllele(chromosome);
        IAllele inactive = individual.getGenome().getInactiveAllele(chromosome);
        textLayout.drawRow(
                transform,
                chromosomeName,
                active.getDisplayName(),
                inactive.getDisplayName(),
                ColourProperties.INSTANCE.get("gui.screen"),
                getColorCoding(active.isDominant()),
                getColorCoding(inactive.isDominant())
        );
    }

    public final void drawSpeciesRow(
            MatrixStack transform,
            ITextProperties text0,
            IIndividual individual,
            IChromosomeType chromosome,
            @Nullable ITextComponent customPrimaryName,
            @Nullable ITextComponent customSecondaryName
    ) {
        IAlleleSpecies primary = individual.getGenome().getPrimary();
        IAlleleSpecies secondary = individual.getGenome().getSecondary();

        textLayout.drawLine(transform, text0, textLayout.column0);
        int columnwidth = textLayout.column2 - textLayout.column1 - 2;

        Map<ResourceLocation, ItemStack> iconStacks = ((IForestrySpeciesRoot) chromosome.getRoot()).getAlyzerPlugin().getIconStacks();

        GuiUtil.drawItemStack(
                this,
                iconStacks.get(primary.getRegistryName()),
                guiLeft + textLayout.column1 + columnwidth - 20,
                guiTop + 10
        );
        GuiUtil.drawItemStack(
                this,
                iconStacks.get(secondary.getRegistryName()),
                guiLeft + textLayout.column2 + columnwidth - 20,
                guiTop + 10
        );

        ITextComponent primaryName = customPrimaryName == null ? primary.getDisplayName() : customPrimaryName;
        ITextComponent secondaryName = customSecondaryName == null ? secondary.getDisplayName() : customSecondaryName;

        drawSplitLine(primaryName, textLayout.column1, columnwidth, individual, chromosome, false);
        drawSplitLine(secondaryName, textLayout.column2, columnwidth, individual, chromosome, true);

        textLayout.newLine();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);
        widgetManager.clear();

        int specimenSlot = getSpecimenSlot();
        if (specimenSlot < ItemInventoryAlyzer.SLOT_ANALYZE_1) {
            drawAnalyticsOverview(transform);
            return;
        }

        ItemStack stackInSlot = itemInventory.getStackInSlot(specimenSlot);
        IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = RootUtils.getRoot(stackInSlot);
        if (!definition.isPresent()) {
            return;
        }

        IForestrySpeciesRoot<IIndividual> speciesRoot = definition.get();
        switch (specimenSlot) {
            case ItemInventoryAlyzer.SLOT_ANALYZE_1: {
                speciesRoot.getAlyzerPlugin().drawAnalyticsPage1(this, stackInSlot, transform);
                break;
            }
            case ItemInventoryAlyzer.SLOT_ANALYZE_2: {
                speciesRoot.getAlyzerPlugin().drawAnalyticsPage2(this, stackInSlot, transform);
                break;
            }
            case ItemInventoryAlyzer.SLOT_ANALYZE_3: {
                speciesRoot.getAlyzerPlugin().drawAnalyticsPage3(this, stackInSlot, transform);
                break;
            }
            case ItemInventoryAlyzer.SLOT_ANALYZE_4: {
                speciesRoot.create(stackInSlot).ifPresent((value) -> drawAnalyticsPageMutations(transform, value));
                break;
            }
            case ItemInventoryAlyzer.SLOT_ANALYZE_5: {
                speciesRoot.create(stackInSlot).ifPresent((value) -> drawAnalyticsPageClassification(transform, value));
                break;
            }
            default:
                drawAnalyticsOverview(transform);
        }

    }

    private int getSpecimenSlot() {
        for (int k = ItemInventoryAlyzer.SLOT_SPECIMEN; k <= ItemInventoryAlyzer.SLOT_ANALYZE_5; k++) {
            ItemStack stackInSlot = itemInventory.getStackInSlot(k);
            if (stackInSlot.isEmpty()) {
                continue;
            }

            IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = RootUtils.getRoot(stackInSlot);
            if (!definition.isPresent()) {
                continue;
            }
            IForestrySpeciesRoot<IIndividual> speciesRoot = definition.get();

            Optional<IIndividual> optionalIndividual = speciesRoot.create(stackInSlot);
            if (optionalIndividual.filter(individual -> !individual.isAnalyzed()).isPresent()) {
                continue;
            }

            return k;
        }
        return -1;
    }

    public void drawAnalyticsOverview(MatrixStack transform) {
        textLayout.startPage();

        textLayout.newLine();
        textLayout.drawCenteredLine(
                transform,
                new TranslationTextComponent("for.gui.portablealyzer"),
                8,
                208,
                ColourProperties.INSTANCE.get("gui.screen")
        );
        textLayout.newLine();

        getFontRenderer().func_238418_a_(
                new TranslationTextComponent("for.gui.portablealyzer.help"),
                guiLeft + COLUMN_0 + 4,
                guiTop + 42,
                200,
                ColourProperties.INSTANCE.get("gui.screen")
        );
        textLayout.newLine();
        textLayout.newLine();
        textLayout.newLine();
        textLayout.newLine();

        textLayout.drawLine(
                transform,
                new TranslationTextComponent("for.gui.alyzer.overview").appendString(":"),
                COLUMN_0 + 4
        );
        textLayout.newLine();
        textLayout.drawLine(transform, new TranslationTextComponent("for.gui.general"), COLUMN_0 + 4);
        textLayout.newLine();
        textLayout.drawLine(transform, new TranslationTextComponent("for.gui.environment"), COLUMN_0 + 4);
        textLayout.newLine();
        textLayout.drawLine(transform, new TranslationTextComponent("for.gui.produce"), COLUMN_0 + 4);
        textLayout.newLine();
        textLayout.drawLine(transform, new TranslationTextComponent("for.gui.evolution"), COLUMN_0 + 4);

        textLayout.endPage();
    }

    public final void drawAnalyticsPageClassification(MatrixStack transform, IIndividual individual) {
        textLayout.startPage();

        textLayout.drawLine(
                transform,
                new TranslationTextComponent("for.gui.alyzer.classification").appendString(":"),
                12
        );
        textLayout.newLine();

        Stack<IClassification> hierarchy = new Stack<>();
        IClassification classification = individual.getGenome().getPrimary().getBranch();
        while (classification != null) {

            if (!classification.getScientific().isEmpty()) {
                hierarchy.push(classification);
            }
            classification = classification.getParent();
        }

        boolean overcrowded = hierarchy.size() > 5;
        int x = 12;
        IClassification group = null;

        while (!hierarchy.isEmpty()) {

            group = hierarchy.pop();
            if (overcrowded && group.getLevel().isDroppable()) {
                continue;
            }

            textLayout.drawLine(
                    transform,
                    ITextComponent.func_241827_a_(group.getScientific()),
                    x,
                    group.getLevel().getColour()
            );
            textLayout.drawLine(
                    transform,
                    ITextComponent.func_241827_a_(group.getLevel().name()),
                    170,
                    group.getLevel().getColour()
            );
            textLayout.newLineCompressed();
            x += 12;
        }

        // Add the species name
        String binomial = individual.getGenome().getPrimary().getBinomial();
        if (group != null && group.getLevel() == IClassification.EnumClassLevel.GENUS) {
            binomial = group.getScientific().charAt(0) + ". " + binomial.toLowerCase(Locale.ENGLISH);
        }

        textLayout.drawLine(transform, ITextComponent.func_241827_a_(binomial), x, 0xebae85);
        textLayout.drawLine(transform, new TranslationTextComponent("for.gui.species"), 170, 0xebae85);

        textLayout.newLine();
        textLayout.drawLine(
                transform,
                new TranslationTextComponent("for.gui.alyzer.authority").appendString(": " + individual.getGenome().getPrimary().getAuthority()),
                12
        );
        if (AlleleUtils.isBlacklisted(individual.getIdentifier())) {
            ITextComponent extinct = new StringTextComponent(">> ")
                    .append(new TranslationTextComponent("for.gui.alyzer.extinct"))
                    .appendString(" <<");
            getFontRenderer().func_238422_b_(
                    transform,
                    extinct,
                    guiLeft + 200 - getFontRenderer().getStringWidth(extinct.getString()),
                    guiTop + textLayout.getLineY(),
                    ColourProperties.INSTANCE.get("gui.beealyzer.dominant")
            );
        }

        textLayout.newLine();
        String description = individual.getGenome().getPrimary().getDescription().getString();
        if (StringUtils.isBlank(description) || description.startsWith("for.description.")) {
            textLayout.drawSplitLine(new TranslationTextComponent("for.gui.alyzer.nodescription"), 12, 200, 0x666666);
        } else {
            String[] tokens = description.split("\\|");
            textLayout.drawSplitLine(ITextComponent.func_241827_a_(tokens[0]), 12, 200, 0x666666);
            if (tokens.length > 1) {
                String signature = "- " + tokens[1];
                getFontRenderer().drawStringWithShadow(
                        transform,
                        signature,
                        guiLeft + 210 - getFontRenderer().getStringWidth(signature),
                        guiTop + 145 - 14,
                        0x99cc32
                );
            }
        }

        textLayout.endPage();
    }

    @SuppressWarnings("unchecked")
    public void drawAnalyticsPageMutations(MatrixStack transform, IIndividual individual) {
        textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);
        textLayout.drawLine(
                transform,
                new TranslationTextComponent("for.gui.beealyzer.mutations").appendString(":"),
                COLUMN_0
        );
        textLayout.newLine();

        //RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light

        IGenome genome = individual.getGenome();
        IForestrySpeciesRoot<IIndividual> speciesRoot = (IForestrySpeciesRoot) individual.getRoot();
        IAlleleSpecies species = genome.getPrimary();

        int columnWidth = 50;
        int x = 0;

        PlayerEntity player = Minecraft.getInstance().player;
        //TODO world cast
        IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());

        IMutationContainer<IIndividual, ? extends IMutation> container = speciesRoot.getComponent(ComponentKeys.MUTATIONS);
        for (IMutation mutation : container.getCombinations(species)) {
            if (breedingTracker.isDiscovered(mutation)) {
                drawMutationInfo(transform, mutation, species, COLUMN_0 + x, breedingTracker);
            } else {
                // Do not display secret undiscovered mutations.
                if (mutation.isSecret()) {
                    continue;
                }

                drawUnknownMutation(transform, mutation, COLUMN_0 + x, breedingTracker);
            }

            x += columnWidth;
            if (x >= columnWidth * 4) {
                x = 0;
                textLayout.newLine(16);
            }
        }

        textLayout.endPage();
    }

    public void drawMutationInfo(
            MatrixStack transform,
            IMutation combination,
            IAllele species,
            int x,
            IBreedingTracker breedingTracker
    ) {
        Map<ResourceLocation, ItemStack> iconStacks = ((IForestrySpeciesRoot) combination.getRoot()).getAlyzerPlugin().getIconStacks();

        ItemStack partnerBee = iconStacks.get(combination.getPartner(species).getRegistryName());
        widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), partnerBee));

        drawProbabilityArrow(
                transform,
                combination,
                guiLeft + x + 18,
                guiTop + textLayout.getLineY() + 4,
                breedingTracker
        );

        IAllele result = combination.getTemplate()[BeeChromosomes.SPECIES.ordinal()];
        ItemStack resultBee = iconStacks.get(result.getRegistryName());
        widgetManager.add(new ItemStackWidget(widgetManager, x + 33, textLayout.getLineY(), resultBee));
    }

    private void drawUnknownMutation(
            MatrixStack transform,
            IMutation combination,
            int x,
            IBreedingTracker breedingTracker
    ) {

        drawQuestionMark(transform, guiLeft + x, guiTop + textLayout.getLineY());

        drawProbabilityArrow(
                transform,
                combination,
                guiLeft + x + 18,
                guiTop + textLayout.getLineY() + 4,
                breedingTracker
        );

        drawQuestionMark(transform, guiLeft + x + 32, guiTop + textLayout.getLineY());
    }

    private void drawQuestionMark(MatrixStack transform, int x, int y) {
        bindTexture(textureFile);
        blit(transform, x, y, 78, 240, 16, 16);
    }

    private void drawProbabilityArrow(
            MatrixStack transform,
            IMutation combination,
            int x,
            int y,
            IBreedingTracker breedingTracker
    ) {
        float chance = combination.getBaseChance();
        int line = 247;
        int column = 100;
        switch (EnumMutateChance.rateChance(chance)) {
            case HIGHEST:
                column = 100;
                break;
            case HIGHER:
                column = 100 + 15;
                break;
            case HIGH:
                column = 100 + 15 * 2;
                break;
            case NORMAL:
                column = 100 + 15 * 3;
                break;
            case LOW:
                column = 100 + 15 * 4;
                break;
            case LOWEST:
                column = 100 + 15 * 5;
            default:
                break;
        }

        // Probability arrow
        bindTexture(textureFile);
        blit(transform, x, y, column, line, 15, 9);

        boolean researched = breedingTracker.isResearched(combination);
        if (researched) {
            getFontRenderer().drawString(transform, "+", x + 9, y + 1, 0);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void drawToleranceInfo(MatrixStack transform, IAlleleValue<EnumTolerance> toleranceAllele, int x) {
        int textColor = getColorCoding(toleranceAllele.isDominant());
        EnumTolerance tolerance = toleranceAllele.getValue();
        ITextProperties text = new StringTextComponent("(")
                .append(toleranceAllele.getDisplayName()).appendString(")");

        // Enable correct lighting.
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        switch (tolerance) {
            case BOTH_1:
            case BOTH_2:
            case BOTH_3:
            case BOTH_4:
            case BOTH_5:
                drawBothSymbol(transform, x - 2, textLayout.getLineY() - 1);
                textLayout.drawLine(transform, text, x + 14, textColor);
                break;
            case DOWN_1:
            case DOWN_2:
            case DOWN_3:
            case DOWN_4:
            case DOWN_5:
                drawDownSymbol(transform, x - 2, textLayout.getLineY() - 1);
                textLayout.drawLine(transform, text, x + 14, textColor);
                break;
            case UP_1:
            case UP_2:
            case UP_3:
            case UP_4:
            case UP_5:
                drawUpSymbol(transform, x - 2, textLayout.getLineY() - 1);
                textLayout.drawLine(transform, text, x + 14, textColor);
                break;
            default:
                drawNoneSymbol(transform, x - 2, textLayout.getLineY() - 1);
                textLayout.drawLine(transform, ITextComponent.func_241827_a_("(0)"), x + 14, textColor);
                break;
        }
    }

    private void drawDownSymbol(MatrixStack transform, int x, int y) {
        bindTexture(textureFile);
        blit(transform, guiLeft + x, guiTop + y, 0, 247, 15, 9);
    }

    private void drawUpSymbol(MatrixStack transform, int x, int y) {
        bindTexture(textureFile);
        blit(transform, guiLeft + x, guiTop + y, 15, 247, 15, 9);
    }

    private void drawBothSymbol(MatrixStack transform, int x, int y) {
        bindTexture(textureFile);
        blit(transform, guiLeft + x, guiTop + y, 30, 247, 15, 9);
    }

    private void drawNoneSymbol(MatrixStack transform, int x, int y) {
        bindTexture(textureFile);
        blit(transform, guiLeft + x, guiTop + y, 45, 247, 15, 9);
    }

    public void drawFertilityInfo(MatrixStack transform, int fertility, int x, int textColor, int texOffset) {
        // Enable correct lighting.
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        String fertilityString = fertility + " x";

        int stringWidth = getFontRenderer().getStringWidth(fertilityString);

        bindTexture(textureFile);
        blit(transform, guiLeft + x + stringWidth + 2, guiTop + textLayout.getLineY() - 1, 60, 240 + texOffset, 12, 8);

        textLayout.drawLine(transform, ITextComponent.func_241827_a_(fertilityString), x, textColor);
    }

    public TextLayoutHelper getTextLayout() {
        return textLayout;
    }

    public WidgetManager getWidgetManager() {
        return widgetManager;
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(itemInventory);
        addHintLedger(getHints());
    }

    public List<String> getHints() {
        ItemStack specimen = itemInventory.getSpecimen();
        if (!specimen.isEmpty()) {
            IRootDefinition<IForestrySpeciesRoot> definition = RootUtils.getRoot(specimen);
            if (definition.isPresent()) {
                IAlyzerPlugin alyzerPlugin = definition.get().getAlyzerPlugin();
                return alyzerPlugin.getHints();
            }
        }
        return Collections.emptyList();
    }
}
