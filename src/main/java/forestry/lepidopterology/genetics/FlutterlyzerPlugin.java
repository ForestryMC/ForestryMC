/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.lepidopterology.genetics;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Config;
import forestry.core.genetics.GenericRatings;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.utils.StringUtil;
import forestry.lepidopterology.features.LepidopterologyItems;
import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.organism.IOrganism;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
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

//TODO: Port plugin
public class FlutterlyzerPlugin implements IAlyzerPlugin {
    public static final FlutterlyzerPlugin INSTANCE = new FlutterlyzerPlugin();

    protected final Map<ResourceLocation, ItemStack> iconStacks = new HashMap<>();

    private FlutterlyzerPlugin() {
        NonNullList<ItemStack> butterflyList = NonNullList.create();
        LepidopterologyItems.BUTTERFLY_GE.item().addCreativeItems(butterflyList, false);
        for (ItemStack butterflyStack : butterflyList) {
            IOrganism<?> organism = GeneticHelper.getOrganism(butterflyStack);
            if (organism.isEmpty()) {
                continue;
            }

            IAlleleButterflySpecies species = organism.getAllele(ButterflyChromosomes.SPECIES, true);
            iconStacks.put(species.getRegistryName(), butterflyStack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage1(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<IButterfly> optional = ButterflyManager.butterflyRoot.create(itemStack);
            if (optional == null) {
                return;
            }

            IButterfly butterfly = optional.get();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.active"), GuiAlyzer.COLUMN_1);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.inactive"), GuiAlyzer.COLUMN_2);

            textLayout.newLine();
            textLayout.newLine();

            guiAlyzer.drawSpeciesRow(
                    transform,
                    new TranslationTextComponent("for.gui.species"),
                    butterfly,
                    ButterflyChromosomes.SPECIES,
                    null,
                    null
            );
            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.size"),
                    butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SIZE).getDisplayName(),
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.SIZE).getDisplayName(),
                    butterfly,
                    ButterflyChromosomes.SPEED
            );
            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.lifespan"),
                    butterfly.getGenome().getActiveAllele(ButterflyChromosomes.LIFESPAN).getDisplayName(),
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.LIFESPAN).getDisplayName(),
                    butterfly,
                    ButterflyChromosomes.LIFESPAN
            );
            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.speed"),
                    butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPEED).getDisplayName(),
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.SPEED).getDisplayName(),
                    butterfly, ButterflyChromosomes.SPEED
            );
            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.metabolism"),
                    GenericRatings.rateMetabolism(butterfly.getGenome()
                                                           .getActiveAllele(ButterflyChromosomes.METABOLISM)
                                                           .getValue()),
                    GenericRatings.rateMetabolism(butterfly.getGenome()
                                                           .getInactiveAllele(ButterflyChromosomes.METABOLISM)
                                                           .getValue()),
                    butterfly,
                    ButterflyChromosomes.METABOLISM
            );
            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.fertility"), GuiAlyzer.COLUMN_0);
            guiAlyzer.drawFertilityInfo(
                    transform,
                    butterfly.getGenome().getActiveAllele(ButterflyChromosomes.FERTILITY).getValue(),
                    GuiAlyzer.COLUMN_1,
                    guiAlyzer.getColorCoding(butterfly.getGenome()
                                                      .getActiveAllele(ButterflyChromosomes.FERTILITY)
                                                      .isDominant()),
                    8
            );
            guiAlyzer.drawFertilityInfo(
                    transform,
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.FERTILITY).getValue(),
                    GuiAlyzer.COLUMN_2,
                    guiAlyzer.getColorCoding(butterfly.getGenome()
                                                      .getInactiveAllele(ButterflyChromosomes.FERTILITY)
                                                      .isDominant()),
                    8
            );

            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.flowers"),
                    butterfly.getGenome()
                             .getActiveAllele(ButterflyChromosomes.FLOWER_PROVIDER)
                             .getProvider()
                             .getDescription(),
                    butterfly.getGenome()
                             .getInactiveAllele(ButterflyChromosomes.FLOWER_PROVIDER)
                             .getProvider()
                             .getDescription(),
                    butterfly,
                    ButterflyChromosomes.FLOWER_PROVIDER
            );
            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.effect"),
                    butterfly.getGenome().getActiveAllele(ButterflyChromosomes.EFFECT).getDisplayName(),
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.EFFECT).getDisplayName(),
                    butterfly,
                    ButterflyChromosomes.EFFECT
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
            Optional<IButterfly> optional = ButterflyManager.butterflyRoot.create(itemStack);
            if (optional == null) {
                return;
            }

            IButterfly butterfly = optional.get();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.active"), GuiAlyzer.COLUMN_1);
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.inactive"), GuiAlyzer.COLUMN_2);

            textLayout.newLine();
            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.climate"),
                    AlleleManager.climateHelper.toDisplay(
                            butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getTemperature()
                    ),
                    AlleleManager.climateHelper.toDisplay(
                            butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.SPECIES).getTemperature()
                    ),
                    butterfly,
                    ButterflyChromosomes.SPECIES
            );
            textLayout.newLine();

            IAlleleValue<EnumTolerance> tempToleranceActive = butterfly.getGenome()
                                                                       .getActiveAllele(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
            IAlleleValue<EnumTolerance> tempToleranceInactive = butterfly.getGenome()
                                                                         .getInactiveAllele(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
            textLayout.drawLine(
                    transform,
                    new StringTextComponent("  ").append(new TranslationTextComponent("for.gui.tolerance")),
                    GuiAlyzer.COLUMN_0
            );
            guiAlyzer.drawToleranceInfo(transform, tempToleranceActive, GuiAlyzer.COLUMN_1);
            guiAlyzer.drawToleranceInfo(transform, tempToleranceInactive, GuiAlyzer.COLUMN_2);

            textLayout.newLine();

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.humidity"),
                    AlleleManager.climateHelper.toDisplay(butterfly.getGenome()
                                                                   .getActiveAllele(ButterflyChromosomes.SPECIES)
                                                                   .getHumidity()),
                    AlleleManager.climateHelper.toDisplay(butterfly.getGenome()
                                                                   .getActiveAllele(ButterflyChromosomes.SPECIES)
                                                                   .getHumidity()),
                    butterfly,
                    ButterflyChromosomes.SPECIES
            );
            textLayout.newLine();

            IAlleleValue<EnumTolerance> humidToleranceActive = butterfly.getGenome()
                                                                        .getActiveAllele(ButterflyChromosomes.HUMIDITY_TOLERANCE);
            IAlleleValue<EnumTolerance> humidToleranceInactive = butterfly.getGenome()
                                                                          .getInactiveAllele(ButterflyChromosomes.HUMIDITY_TOLERANCE);
            textLayout.drawLine(
                    transform,
                    new StringTextComponent("  ").append(new TranslationTextComponent("for.gui.tolerance")),
                    GuiAlyzer.COLUMN_0
            );
            guiAlyzer.drawToleranceInfo(transform, humidToleranceActive, GuiAlyzer.COLUMN_1);
            guiAlyzer.drawToleranceInfo(transform, humidToleranceInactive, GuiAlyzer.COLUMN_2);

            textLayout.newLine();
            textLayout.newLine();

            ITextComponent yes = new TranslationTextComponent("for.yes");
            ITextComponent no = new TranslationTextComponent("for.no");

            ITextComponent diurnal0, diurnal1, nocturnal0, nocturnal1;
            if (butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.NOCTURNAL).getValue()) {
                nocturnal0 = diurnal0 = yes;
            } else {
                nocturnal0 = butterfly.getGenome()
                                      .getActiveAllele(ButterflyChromosomes.NOCTURNAL)
                                      .getValue() ? yes : no;
                diurnal0 = !butterfly.getGenome().getActiveAllele(ButterflyChromosomes.NOCTURNAL).getValue() ? yes : no;
            }

            if (butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.NOCTURNAL).getValue()) {
                nocturnal1 = diurnal1 = yes;
            } else {
                nocturnal1 = butterfly.getGenome()
                                      .getInactiveAllele(ButterflyChromosomes.NOCTURNAL)
                                      .getValue() ? yes : no;
                diurnal1 = !butterfly.getGenome()
                                     .getInactiveAllele(ButterflyChromosomes.NOCTURNAL)
                                     .getValue() ? yes : no;
            }

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.diurnal"), GuiAlyzer.COLUMN_0);
            textLayout.drawLine(transform, diurnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
            textLayout.drawLine(transform, diurnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.nocturnal"), GuiAlyzer.COLUMN_0);
            textLayout.drawLine(transform, nocturnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
            textLayout.drawLine(transform, nocturnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
            textLayout.newLine();

            ITextComponent primary = StringUtil.readableBoolean(
                    butterfly.getGenome()
                             .getActiveValue(ButterflyChromosomes.TOLERANT_FLYER),
                    yes,
                    no
            );
            ITextComponent secondary = StringUtil.readableBoolean(
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.TOLERANT_FLYER).getValue(),
                    yes,
                    no
            );

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.flyer"),
                    primary,
                    secondary,
                    butterfly,
                    ButterflyChromosomes.TOLERANT_FLYER
            );
            textLayout.newLine();

            primary = StringUtil.readableBoolean(
                    butterfly.getGenome().getActiveValue(ButterflyChromosomes.FIRE_RESIST),
                    yes,
                    no
            );
            secondary = StringUtil.readableBoolean(
                    butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.FIRE_RESIST).getValue(),
                    yes,
                    no
            );

            guiAlyzer.drawRow(
                    transform,
                    new TranslationTextComponent("for.gui.fireresist"),
                    primary,
                    secondary,
                    butterfly,
                    ButterflyChromosomes.FIRE_RESIST
            );

            textLayout.endPage();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawAnalyticsPage3(Screen gui, ItemStack itemStack, MatrixStack transform) {
        if (gui instanceof GuiAlyzer) {
            GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
            Optional<IButterfly> optional = ButterflyManager.butterflyRoot.create(itemStack);
            if (optional == null) {
                return;
            }

            IButterfly butterfly = optional.get();

            TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

            textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui.loot.butterfly").appendString(":"),
                    GuiAlyzer.COLUMN_0
            );
            textLayout.newLine();

            int x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : butterfly.getGenome()
                                            .getActiveAllele(ButterflyChromosomes.SPECIES)
                                            .getButterflyLoot()
                                            .getPossibleStacks()) {
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

            textLayout.newLine();
            textLayout.newLine();

            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui.loot.caterpillar").appendString(":"),
                    GuiAlyzer.COLUMN_0
            );
            textLayout.newLine();

            x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : butterfly.getGenome()
                                            .getActiveAllele(ButterflyChromosomes.SPECIES)
                                            .getCaterpillarLoot()
                                            .getPossibleStacks()) {
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

            textLayout.newLine();
            textLayout.newLine();

            textLayout.drawLine(
                    transform,
                    new TranslationTextComponent("for.gui.loot.cocoon").appendString(":"),
                    GuiAlyzer.COLUMN_0
            );
            textLayout.newLine();

            x = GuiAlyzer.COLUMN_0;
            for (ItemStack stack : butterfly.getGenome()
                                            .getActiveAllele(ButterflyChromosomes.COCOON)
                                            .getCocoonLoot()
                                            .getPossibleStacks()) {
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
        return Config.hints.get("flutterlyzer");
    }
}
