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
package forestry.apiculture.inventory;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.apiculture.genetics.Bee;
import forestry.core.inventory.ItemInventory;
import genetics.api.GeneticHelper;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemInventoryImprinter extends ItemInventory {
    private static final short specimenSlot = 0;
    private static final short imprintedSlot = 1;

    private int primaryIndex = 0;
    private int secondaryIndex = 0;

    public ItemInventoryImprinter(PlayerEntity player, ItemStack itemStack) {
        super(player, 2, itemStack);
    }

    public void advancePrimary() {
        if (primaryIndex < BeeManager.beeRoot.getIndividualTemplates().size() - 1) {
            primaryIndex++;
        } else {
            primaryIndex = 0;
        }
    }

    public void advanceSecondary() {
        if (secondaryIndex < BeeManager.beeRoot.getIndividualTemplates().size() - 1) {
            secondaryIndex++;
        } else {
            secondaryIndex = 0;
        }
    }

    public void regressPrimary() {
        if (primaryIndex > 0) {
            primaryIndex--;
        } else {
            primaryIndex = BeeManager.beeRoot.getIndividualTemplates().size() - 1;
        }
    }

    public void regressSecondary() {
        if (secondaryIndex > 0) {
            secondaryIndex--;
        } else {
            secondaryIndex = BeeManager.beeRoot.getIndividualTemplates().size() - 1;
        }
    }

    public IAlleleBeeSpecies getPrimary() {
        return BeeManager.beeRoot.getIndividualTemplates()
                                 .get(primaryIndex)
                                 .getGenome()
                                 .getActiveAllele(BeeChromosomes.SPECIES);
    }

    public IAlleleBeeSpecies getSecondary() {
        return BeeManager.beeRoot.getIndividualTemplates().get(secondaryIndex).getGenome().getActiveAllele(
                BeeChromosomes.SPECIES);
    }

    public IBee getSelectedBee() {
        IBeeRoot beeRoot = BeeManager.beeRoot;
        List<IBee> individualTemplates = beeRoot.getIndividualTemplates();
        Map<String, IAllele[]> genomeTemplates = beeRoot.getTemplates().getGenomeTemplates();
        IAllele[] templateActive = genomeTemplates.get(individualTemplates.get(primaryIndex).getIdentifier());
        IAllele[] templateInactive = genomeTemplates.get(individualTemplates.get(secondaryIndex).getIdentifier());
        IGenome genome = beeRoot.getKaryotype().templateAsGenome(templateActive, templateInactive);
        return new Bee(genome);
    }

    public int getPrimaryIndex() {
        return primaryIndex;
    }

    public int getSecondaryIndex() {
        return secondaryIndex;
    }

    public void setPrimaryIndex(int index) {
        primaryIndex = index;
    }

    public void setSecondaryIndex(int index) {
        secondaryIndex = index;
    }

    @Override
    public void onSlotClick(int slotIndex, PlayerEntity player) {
        ItemStack specimen = getStackInSlot(specimenSlot);
        if (specimen.isEmpty()) {
            return;
        }

        // Only imprint bees
        if (!BeeManager.beeRoot.isMember(specimen)) {
            return;
        }

        // Needs space
        if (!getStackInSlot(imprintedSlot).isEmpty()) {
            return;
        }

        IBee imprint = getSelectedBee();

        GeneticHelper.setIndividual(specimen, imprint);

        setInventorySlotContents(imprintedSlot, specimen);
        setInventorySlotContents(specimenSlot, ItemStack.EMPTY);
    }

    //TODO inventory name
    //	@Override
    //	public String getName() {
    //		return "Imprinter";
    //	}

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        return BeeManager.beeRoot.isMember(itemStack);
    }

}
