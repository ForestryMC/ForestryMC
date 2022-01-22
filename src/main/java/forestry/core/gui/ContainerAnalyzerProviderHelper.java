package forestry.core.gui;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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

//import forestry.database.inventory.InventoryDatabaseAnalyzer;

public class ContainerAnalyzerProviderHelper {
	/* Attributes - Final*/
	private final Player player;
	private final ContainerForestry container;
	@Nullable
	private final ItemInventoryAlyzer alyzerInventory;

	public ContainerAnalyzerProviderHelper(ContainerForestry container, Inventory playerInventory) {
		this.player = playerInventory.player;
		this.container = container;

		ItemInventoryAlyzer alyzerInventory = null;
		int analyzerIndex = -1;
		for (int i = 0; i < playerInventory.getContainerSize(); i++) {
			ItemStack stack = playerInventory.getItem(i);
			if (stack.isEmpty() || !CoreItems.PORTABLE_ALYZER.itemEqual(stack)) {
				continue;
			}
			analyzerIndex = i;
			alyzerInventory = new ItemInventoryAlyzer(playerInventory.player, stack);
			Slot slot = container.getSlot(i < 9 ? i + 27 : i - 9);
			if (slot instanceof SlotLockable lockable) {
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
		if (alyzerInventory == null) {
			return null;
		}
		for (Slot slot : container.slots) {
			if (slot instanceof SlotAnalyzer) {
				return slot;
			}
		}
		return null;
	}

	public void analyzeSpecimen(int selectedSlot) {
		if (selectedSlot < 0 || alyzerInventory == null) {
			return;
		}
		Slot specimenSlot = container.getForestrySlot(selectedSlot);
		ItemStack specimen = specimenSlot.getItem();
		if (specimen.isEmpty()) {
			return;
		}

		ItemStack convertedSpecimen = GeneticsUtil.convertToGeneticEquivalent(specimen);
		if (!ItemStack.matches(specimen, convertedSpecimen)) {
			specimenSlot.set(convertedSpecimen);
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
				ItemStack energyStack = alyzerInventory.getItem(InventoryDatabaseAnalyzer.SLOT_ENERGY);
				if (requiresEnergy && !ItemInventoryAlyzer.isAlyzingFuel(energyStack)) {
					return;
				}

				if (individual.analyze()) {
					IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.level, player.getGameProfile());
					breedingTracker.registerSpecies(individual.getGenome().getPrimary());
					breedingTracker.registerSpecies(individual.getGenome().getSecondary());

					specimen = specimen.copy();
					GeneticHelper.setIndividual(specimen, individual);

					if (requiresEnergy) {
						// Decrease energy
						alyzerInventory.removeItem(InventoryDatabaseAnalyzer.SLOT_ENERGY, 1);
					}
				}
				specimenSlot.set(specimen);
			}
		}
	}
}
