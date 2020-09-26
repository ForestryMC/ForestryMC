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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.buttons.GuiBetterButton;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GuiNaturalistInventory extends GuiForestry<ContainerNaturalistInventory> {
    private final IForestrySpeciesRoot<IIndividual> speciesRoot;
    private final IBreedingTracker breedingTracker;
    private final HashMap<String, ItemStack> iconStacks = new HashMap<>();
    private final int pageCurrent, pageMax;
    private final CycleTimer timer = new CycleTimer(0);

    public GuiNaturalistInventory(ContainerNaturalistInventory container, PlayerInventory playerInv, ITextComponent name) {
        super(Constants.TEXTURE_PATH_GUI + "/apiaristinventory.png", container, playerInv, name);

        this.speciesRoot = container.tile.getSpeciesRoot();

        this.pageCurrent = container.getPage();
        this.pageMax = container.getMaxPage();

        xSize = 196;
        ySize = 202;

        for (IIndividual individual : speciesRoot.getIndividualTemplates()) {
            iconStacks.put(individual.getIdentifier(), speciesRoot.getTypes().createStack(individual, speciesRoot.getIconType()));
        }

        breedingTracker = speciesRoot.getBreedingTracker(playerInv.player.world, playerInv.player.getGameProfile());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int j, int i) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, j, i);
        timer.onDraw();
        ITextComponent header = new TranslationTextComponent("for.gui.page").appendString(" " + (pageCurrent + 1) + "/" + pageMax);
        getFontRenderer().func_243248_b(
                transform,
                header,
                guiLeft + 95 + textLayout.getCenteredOffset(header, 98),
                guiTop + 10,
                ColourProperties.INSTANCE.get("gui.title")
        );

        IIndividual individual = getIndividualAtPosition(i, j);
        if (individual == null) {
            displayBreedingStatistics(transform, 10);
        }

        if (individual != null) {
            //RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light
            textLayout.startPage();

            IGenome genome = individual.getGenome();
            IChromosomeType speciesType = individual.getRoot().getKaryotype().getSpeciesType();
            boolean pureBred = individual.isPureBred(speciesType);

            displaySpeciesInformation(transform, true, genome.getPrimary(), iconStacks.get(individual.getIdentifier()), 10, pureBred ? 25 : 10);
            if (!pureBred) {
                displaySpeciesInformation(transform, individual.isAnalyzed(), genome.getSecondary(), iconStacks.get(genome.getSecondary().getRegistryName().toString()), 10, 10);
            }

            textLayout.endPage();
        }
    }

    @Override
    public void init() {
        super.init();

        addButton(new GuiBetterButton(guiLeft + 99, guiTop + 7, StandardButtonTextureSets.LEFT_BUTTON_SMALL, b -> {
            if (pageCurrent > 0) {
                flipPage(pageCurrent - 1);
            }
        }));
        addButton(new GuiBetterButton(guiLeft + 180, guiTop + 7, StandardButtonTextureSets.RIGHT_BUTTON_SMALL, b -> {
            if (pageCurrent < pageMax - 1) {
                flipPage(pageCurrent + 1);
            }
        }));
    }

    private static void flipPage(int page) {
        NetworkUtil.sendToServer(new PacketGuiSelectRequest(page, 0));
    }

    @Nullable
    private IIndividual getIndividualAtPosition(int x, int y) {
        Slot slot = getSlotAtPosition(x, y);
        if (slot == null) {
            return null;
        }

        if (!slot.getHasStack()) {
            return null;
        }

        if (!slot.getStack().hasTag()) {
            return null;
        }

        if (!speciesRoot.isMember(slot.getStack())) {
            return null;
        }

        return speciesRoot.getTypes().createIndividual(slot.getStack()).orElse(null);
    }

    private void displayBreedingStatistics(MatrixStack transform, int x) {

        textLayout.startPage();

        textLayout.drawLine(
                transform,
                new TranslationTextComponent("for.gui.speciescount").appendString(": " + breedingTracker.getSpeciesBred() + "/" + speciesRoot.getSpeciesCount()),
                x
        );
        textLayout.newLine();
        textLayout.newLine();

        if (breedingTracker instanceof IApiaristTracker) {
            IApiaristTracker tracker = (IApiaristTracker) breedingTracker;
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.queens").appendString(": " + tracker.getQueenCount()), x);
            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.princesses").appendString(": " + tracker.getPrincessCount()), x);
            textLayout.newLine();

            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.drones").appendString(": " + tracker.getDroneCount()), x);
            textLayout.newLine();
        }

        textLayout.endPage();
    }

    private void displaySpeciesInformation(MatrixStack transform, boolean analyzed, IAlleleSpecies species, ItemStack iconStack, int x, int maxMutationCount) {

        if (!analyzed) {
            textLayout.drawLine(transform, new TranslationTextComponent("for.gui.unknown"), x);
            return;
        }

        textLayout.drawLine(transform, species.getDisplayName(), x);
        GuiUtil.drawItemStack(this, iconStack, guiLeft + x + 69, guiTop + textLayout.getLineY() - 2);

        textLayout.newLine();

        // Viable Combinations
        int columnWidth = 16;
        int column = 10;

        IMutationContainer<IIndividual, ? extends IMutation> container = speciesRoot.getComponent(ComponentKeys.MUTATIONS);
        List<List<? extends IMutation>> mutations = splitMutations(container.getCombinations(species), maxMutationCount);
        for (IMutation combination : timer.getCycledItem(mutations, Collections::emptyList)) {
            if (combination.isSecret()) {
                continue;
            }

            if (breedingTracker.isDiscovered(combination)) {
                drawMutationIcon(transform, combination, species, column);
            } else {
                drawUnknownIcon(transform, combination, column);
            }

            column += columnWidth;
            if (column > 75) {
                column = 10;
                textLayout.newLine(18);
            }
        }

        textLayout.newLine();
        textLayout.newLine();
    }

    private void drawMutationIcon(MatrixStack transform, IMutation combination, IAlleleSpecies species, int x) {
        GuiUtil.drawItemStack(this, iconStacks.get(combination.getPartner(species).getRegistryName().toString()), guiLeft + x, guiTop + textLayout.getLineY());

        int line = 48;
        int column;
        EnumMutateChance chance = EnumMutateChance.rateChance(combination.getBaseChance());
        if (chance == EnumMutateChance.HIGHEST) {
            line += 16;
            column = 228;
        } else if (chance == EnumMutateChance.HIGHER) {
            line += 16;
            column = 212;
        } else if (chance == EnumMutateChance.HIGH) {
            line += 16;
            column = 196;
        } else if (chance == EnumMutateChance.NORMAL) {
            line += 0;
            column = 228;
        } else if (chance == EnumMutateChance.LOW) {
            line += 0;
            column = 212;
        } else {
            line += 0;
            column = 196;
        }

        bindTexture(textureFile);
        blit(transform, guiLeft + x, guiTop + textLayout.getLineY(), column, line, 16, 16);

    }

    private void drawUnknownIcon(MatrixStack transform, IMutation mutation, int x) {
        float chance = mutation.getBaseChance();

        int line;
        int column;
        if (chance >= 20) {
            line = 16;
            column = 228;
        } else if (chance >= 15) {
            line = 16;
            column = 212;
        } else if (chance >= 12) {
            line = 16;
            column = 196;
        } else if (chance >= 10) {
            line = 0;
            column = 228;
        } else if (chance >= 5) {
            line = 0;
            column = 212;
        } else {
            line = 0;
            column = 196;
        }

        bindTexture(textureFile);
        blit(transform, guiLeft + x, guiTop + textLayout.getLineY(), column, line, 16, 16);
    }

    private static List<List<? extends IMutation>> splitMutations(List<? extends IMutation> mutations, int maxMutationCount) {
        int size = mutations.size();
        if (size <= maxMutationCount) {
            return Collections.singletonList(mutations);
        }
        ImmutableList.Builder<List<? extends IMutation>> subGroups = new ImmutableList.Builder<>();
        List<IMutation> subList = new LinkedList<>();
        subGroups.add(subList);
        int count = 0;
        for (IMutation mutation : mutations) {
            if (mutation.isSecret()) {
                continue;
            }
            if (count % maxMutationCount == 0 && count != 0) {
                subList = new LinkedList<>();
                subGroups.add(subList);
            }
            subList.add(mutation);
            count++;
        }
        return subGroups.build();
    }

    @Override
    protected void addLedgers() {
        addHintLedger("naturalist.chest");
    }
}
