package forestry.core.gui;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.features.CoreItems;
import forestry.core.gui.slots.SlotAnalyzer;
import forestry.core.gui.slots.SlotLockable;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.GeneticsUtil;
import forestry.database.inventory.InventoryDatabaseAnalyzer;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;
import genetics.utils.RootUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

//import forestry.database.inventory.InventoryDatabaseAnalyzer;

public class ContainerAnalyzerProviderHelper {
    /* Attributes - Final*/
    private final PlayerEntity player;
    private final ContainerForestry container;
    @Nullable
    private final ItemInventoryAlyzer alyzerInventory;

    public ContainerAnalyzerProviderHelper(ContainerForestry container, PlayerInventory playerInventory) {
        this.player = playerInventory.player;
        this.container = container;

        ItemInventoryAlyzer alyzerInventory = null;
        int analyzerIndex = -1;
        for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
            ItemStack stack = playerInventory.getStackInSlot(i);
            if (stack.isEmpty() || !CoreItems.PORTABLE_ALYZER.itemEqual(stack)) {
                continue;
            }
            analyzerIndex = i;
            alyzerInventory = new ItemInventoryAlyzer(playerInventory.player, stack);
            Slot slot = container.getSlot(i);
            if (slot instanceof SlotLockable) {
                SlotLockable lockable = (SlotLockable) slot;
                lockable.lock();
            }
            break;
        }
        int analyzerIndex1 = analyzerIndex;
        this.alyzerInventory = alyzerInventory;

        if (alyzerInventory != null) {
            container.addSlot(new SlotAnalyzer(alyzerInventory, ItemInventoryAlyzer.SLOT_ENERGY, -110, 20));
        }
    }

    @Nullable
    public Slot getAnalyzerSlot() {
        return container.inventorySlots.stream().filter(slot -> slot instanceof SlotAnalyzer).findFirst().orElse(null);
    }

    public void analyzeSpecimen(int selectedSlot) {
        if (selectedSlot < 0 || alyzerInventory == null) {
            return;
        }
        Slot specimenSlot = container.getForestrySlot(selectedSlot);
        ItemStack specimen = specimenSlot.getStack();
        if (specimen.isEmpty()) {
            return;
        }

        ItemStack convertedSpecimen = GeneticsUtil.convertToGeneticEquivalent(specimen);
        if (!ItemStack.areItemStacksEqual(specimen, convertedSpecimen)) {
            specimenSlot.putStack(convertedSpecimen);
            specimen = convertedSpecimen;
        }

        IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = RootUtils.getRoot(specimen);
        // No individual, abort
        if (!definition.isPresent()) {
            return;
        }
        IForestrySpeciesRoot<IIndividual> speciesRoot = definition.get();

        Optional<IIndividual> optionalIndividual = speciesRoot.create(specimen);

        // Analyze if necessary
        if (optionalIndividual.isPresent()) {
            IIndividual individual = optionalIndividual.get();
            if (!individual.isAnalyzed()) {
                final boolean requiresEnergy = ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE);
                ItemStack energyStack = alyzerInventory.getStackInSlot(InventoryDatabaseAnalyzer.SLOT_ENERGY);
                if (requiresEnergy && !ItemInventoryAlyzer.isAlyzingFuel(energyStack)) {
                    return;
                }

                if (individual.analyze()) {
                    IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(
                            player.world,
                            player.getGameProfile()
                    );
                    breedingTracker.registerSpecies(individual.getGenome().getPrimary());
                    breedingTracker.registerSpecies(individual.getGenome().getSecondary());

                    specimen = specimen.copy();
                    GeneticHelper.setIndividual(specimen, individual);

                    if (requiresEnergy) {
                        // Decrease energy
                        alyzerInventory.decrStackSize(InventoryDatabaseAnalyzer.SLOT_ENERGY, 1);
                    }
                }
                specimenSlot.putStack(specimen);
            }
        }
    }
}
