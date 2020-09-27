package forestry.apiculture.genetics;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.apiculture.features.ApicultureItems;
import forestry.core.config.Config;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.StringUtil;
import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IGenome;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BeeAlyzerPlugin implements IAlyzerPlugin {
    public static final BeeAlyzerPlugin INSTANCE = new BeeAlyzerPlugin();

    protected final Map<ResourceLocation, ItemStack> iconStacks = new HashMap<>();

    private BeeAlyzerPlugin() {
        NonNullList<ItemStack> beeList = NonNullList.create();
        ApicultureItems.BEE_DRONE.item().addCreativeItems(beeList, false);
        for (ItemStack beeStack : beeList) {
            IOrganism<?> organism = GeneticHelper.getOrganism(beeStack);
            if (organism.isEmpty()) {
                continue;
            }
            IAlleleBeeSpecies species = organism.getAllele(BeeChromosomes.SPECIES, true);
            iconStacks.put(species.getRegistryName(), beeStack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage1(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<IBee> optional = BeeManager.beeRoot.create(itemStack);
            if (!optional.isPresent()) {
                return;
            }
            IBee bee = optional.get();
            Optional<IOrganismType> typeOptional = BeeManager.beeRoot.getTypes().getType(itemStack);
            if (!typeOptional.isPresent()) {
                return;
            }
            IOrganismType type = typeOptional.get();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.active"), GuiAlyzer.COLUMN_1);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.inactive"), GuiAlyzer.COLUMN_2);

            textLayout.newLine();
            textLayout.newLine();

            {
                String customPrimaryBeeKey = "for.bees.custom.beealyzer." + type.getName() + "." +
                                             bee.getGenome().getPrimary().getLocalisationKey().replace(
                                                     "bees.species.",
                                                     ""
                                             );
                String customSecondaryBeeKey = "for.bees.custom.beealyzer." + type.getName() + "." +
                                               bee.getGenome().getSecondary().getLocalisationKey().replace(
                                                       "bees.species.",
                                                       ""
                                               );

                guiAlyzer.drawSpeciesRow(
                        transform,
                        new TranslationTextComponent("for.gui.species"),
                        bee,
                        BeeChromosomes.SPECIES,
                        I18n.hasKey(customPrimaryBeeKey) ? new TranslationTextComponent(customPrimaryBeeKey) : null,
                        I18n.hasKey(customSecondaryBeeKey) ? new TranslationTextComponent(customSecondaryBeeKey) : null
                );
                textLayout.newLine();
            }

            guiAlyzer.drawChromosomeRow(
                    transform,
                    new TranslationTextComponent("for.gui.lifespan"),
                    bee,
                    BeeChromosomes.LIFESPAN
            );
            textLayout.newLine();
            guiAlyzer.drawChromosomeRow(
                    transform,
                    new TranslationTextComponent("for.gui.speed"),
                    bee,
                    BeeChromosomes.SPEED
            );
            textLayout.newLine();
            guiAlyzer.drawChromosomeRow(
                    transform,
                    new TranslationTextComponent("for.gui.pollination"),
                    bee,
                    BeeChromosomes.FLOWERING
            );
            textLayout.newLine();
            guiAlyzer.drawChromosomeRow(
                    transform,
                    new TranslationTextComponent("for.gui.flowers"),
                    bee,
                    BeeChromosomes.FLOWER_PROVIDER
            );
            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.fertility"), GuiAlyzer.COLUMN_0);
            IAlleleValue<Integer> primaryFertility = bee.getGenome().getActiveAllele(BeeChromosomes.FERTILITY);
            IAlleleValue<Integer> secondaryFertility = bee.getGenome().getInactiveAllele(BeeChromosomes.FERTILITY);
            guiAlyzer.drawFertilityInfo(
                    transform,
                    primaryFertility.getValue(),
                    GuiAlyzer.COLUMN_1,
                    guiAlyzer.getColorCoding(primaryFertility.isDominant()),
                    0
            );
            guiAlyzer.drawFertilityInfo(
                    transform,
                    secondaryFertility.getValue(),
                    GuiAlyzer.COLUMN_2,
                    guiAlyzer.getColorCoding(secondaryFertility.isDominant()),
                    0
            );
            textLayout.newLine();

            guiAlyzer.drawChromosomeRow(
                    transform,
                    new TranslationTextComponent("for.gui.area"),
                    bee,
                    BeeChromosomes.TERRITORY
            );
            textLayout.newLine();

            guiAlyzer.drawChromosomeRow(
                    transform,
                    new TranslationTextComponent("for.gui.effect"),
                    bee,
                    BeeChromosomes.EFFECT
            );
            textLayout.newLine();

            textLayout.endPage();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage2(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<IBee> optional = BeeManager.beeRoot.create(itemStack);
            if (!optional.isPresent()) {
                return;
            }
            IBee bee = optional.get();

            Optional<IOrganismType> typeOptional = BeeManager.beeRoot.getTypes().getType(itemStack);
            if (!typeOptional.isPresent()) {
                return;
            }
            IOrganismType type = typeOptional.get();

            IGenome genome = bee.getGenome();
            IAlleleBeeSpecies primaryAllele = genome.getActiveAllele(BeeChromosomes.SPECIES);
            IAlleleBeeSpecies secondaryAllele = genome.getActiveAllele(BeeChromosomes.SPECIES);

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.active"), GuiAlyzer.COLUMN_1);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.inactive"), GuiAlyzer.COLUMN_2);

            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.climate"),
                    AlleleManager.climateHelper.toDisplay(primaryAllele.getTemperature()),
                    AlleleManager.climateHelper.toDisplay(secondaryAllele.getTemperature()),
                    bee,
                    BeeChromosomes.SPECIES
            );

            textLayout.newLine();

            IAlleleValue<EnumTolerance> tempToleranceActive = bee.getGenome()
                                                                 .getActiveAllele(BeeChromosomes.TEMPERATURE_TOLERANCE);
            IAlleleValue<EnumTolerance> tempToleranceInactive = bee.getGenome()
                                                                   .getInactiveAllele(BeeChromosomes.TEMPERATURE_TOLERANCE);
            textLayout.drawLine(
                    transform,
                    new StringTextComponent("  ").append(new TranslationTextComponent("for.gui.tolerance")),
                    GuiAlyzer.COLUMN_0
            );
            guiAlyzer.drawToleranceInfo(transform, tempToleranceActive, GuiAlyzer.COLUMN_1);
            guiAlyzer.drawToleranceInfo(transform, tempToleranceInactive, GuiAlyzer.COLUMN_2);

            textLayout.newLine(16);

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.humidity"),
                    AlleleManager.climateHelper.toDisplay(primaryAllele.getHumidity()),
                    AlleleManager.climateHelper.toDisplay(secondaryAllele.getHumidity()),
                    bee,
                    BeeChromosomes.SPECIES
            );

            textLayout.newLine();

            IAlleleValue<EnumTolerance> humidToleranceActive = bee.getGenome()
                                                                  .getActiveAllele(BeeChromosomes.HUMIDITY_TOLERANCE);
            IAlleleValue<EnumTolerance> humidToleranceInactive = bee.getGenome()
                                                                    .getInactiveAllele(BeeChromosomes.HUMIDITY_TOLERANCE);
            textLayout.drawLine(
                    transform,
                    new StringTextComponent("  ").append(new TranslationTextComponent("for.gui.tolerance")),
                    GuiAlyzer.COLUMN_0
            );
            guiAlyzer.drawToleranceInfo(transform, humidToleranceActive, GuiAlyzer.COLUMN_1);
            guiAlyzer.drawToleranceInfo(transform, humidToleranceInactive, GuiAlyzer.COLUMN_2);

            textLayout.newLine(16);

            ITextComponent yes = new TranslationTextComponent("for.yes");
            ITextComponent no = new TranslationTextComponent("for.no");

            ITextComponent diurnal0, diurnal1, nocturnal0, nocturnal1;
            if (genome.getActiveValue(BeeChromosomes.NEVER_SLEEPS)) {
                nocturnal0 = diurnal0 = yes;
            } else {
                nocturnal0 = primaryAllele.isNocturnal() ? yes : no;
                diurnal0 = !primaryAllele.isNocturnal() ? yes : no;
            }
            if (genome.getInactiveValue(BeeChromosomes.NEVER_SLEEPS)) {
                nocturnal1 = diurnal1 = yes;
            } else {
                nocturnal1 = secondaryAllele.isNocturnal() ? yes : no;
                diurnal1 = !secondaryAllele.isNocturnal() ? yes : no;
            }

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.diurnal"), GuiAlyzer.COLUMN_0);
            textLayout.drawLine(transform, diurnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
            textLayout.drawLine(transform, diurnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
            textLayout.newLineCompressed();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.nocturnal"), GuiAlyzer.COLUMN_0);
            textLayout.drawLine(transform, nocturnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
            textLayout.drawLine(transform, nocturnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
            textLayout.newLineCompressed();

            ITextComponent primary = StringUtil.readableBoolean(
                    genome.getActiveValue(BeeChromosomes.TOLERATES_RAIN),
                    yes,
                    no
            );
            ITextComponent secondary = StringUtil.readableBoolean(
                    genome.getInactiveValue(BeeChromosomes.TOLERATES_RAIN),
                    yes,
                    no
            );

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.flyer"),
                    primary,
                    secondary,
                    bee,
                    BeeChromosomes.TOLERATES_RAIN
            );

            textLayout.newLineCompressed();

            primary = StringUtil.readableBoolean(genome.getActiveValue(BeeChromosomes.CAVE_DWELLING), yes, no);
            secondary = StringUtil.readableBoolean(genome.getInactiveValue(BeeChromosomes.CAVE_DWELLING), yes, no);

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.cave"),
                    primary,
                    secondary,
                    bee,
                    BeeChromosomes.CAVE_DWELLING
            );

            textLayout.newLine();

            if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
                String displayTextKey = "for.bees.stock.pristine";
                if (!bee.isNatural()) {
                    displayTextKey = "for.bees.stock.ignoble";
                }

                textLayout.drawCenteredLine(
                        transform,
                        new TranslationTextComponent(displayTextKey),
                        8,
                        208,
                        guiAlyzer.getFontColor().get("gui.beealyzer.binomial")
                );
            }

            if (bee.getGeneration() >= 0) {
                textLayout.newLineCompressed();
                textLayout.drawCenteredLine(
                        transform,
                        new TranslationTextComponent("for.gui.beealyzer.generations", bee.getGeneration()),
                        8,
                        208,
                        guiAlyzer.getFontColor().get("gui.beealyzer.binomial")
                );
            }

            textLayout.endPage();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage3(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<IBee> optional = BeeManager.beeRoot.create(itemStack);
            if (!optional.isPresent()) {
                return;
            }
            IBee bee = optional.get();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
            WidgetManager widgetManager = guiAlyzer.getWidgetManager();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui.beealyzer.produce").appendString(":"),
                    GuiAlyzer.COLUMN_0
            );

            textLayout.newLine();

            int x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : bee.getProduceList()) {
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

            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui.beealyzer.specialty").appendString(":"),
                    GuiAlyzer.COLUMN_0
            );
            textLayout.newLine();

            x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : bee.getSpecialtyList()) {
                widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));

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
        return Config.hints.get("beealyzer");
    }

}
