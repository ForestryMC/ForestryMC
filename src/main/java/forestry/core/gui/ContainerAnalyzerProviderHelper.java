package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.ModuleCore;
import forestry.core.gui.slots.SlotAnalyzer;
import forestry.core.gui.slots.SlotLockable;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.GeneticsUtil;
import forestry.database.inventory.InventoryDatabaseAnalyzer;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class ContainerAnalyzerProviderHelper {
	/* Attributes - Final*/
	private final EntityPlayer player;
	private final ContainerForestry container;
	@Nullable
	private final ItemInventoryAlyzer alyzerInventory;
	private final int analyzerIndex;

	public ContainerAnalyzerProviderHelper(ContainerForestry container, InventoryPlayer playerInventory) {
		this.player = playerInventory.player;
		this.container = container;

		ItemInventoryAlyzer alyzerInventory = null;
		int analyzerIndex = -1;
		for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
			ItemStack stack = playerInventory.getStackInSlot(i);
			if (stack.isEmpty() || stack.getItem() != ModuleCore.getItems().portableAlyzer) {
				continue;
			}
			analyzerIndex = i;
			alyzerInventory = new ItemInventoryAlyzer(playerInventory.player, stack);
			Slot slot = container.getSlotFromInventory(playerInventory, i);
			if (slot instanceof SlotLockable) {
				SlotLockable lockable = (SlotLockable) slot;
				lockable.lock();
			}
			break;
		}
		this.analyzerIndex = analyzerIndex;
		this.alyzerInventory = alyzerInventory;

		if (alyzerInventory != null) {
			container.addSlotToContainer(new SlotAnalyzer(alyzerInventory, ItemInventoryAlyzer.SLOT_ENERGY, -110, 20));
		}
	}

	@Nullable
	public Slot getAnalyzerSlot() {
		if (alyzerInventory == null) {
			return null;
		}
		return container.getSlotFromInventory(alyzerInventory, 0);
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

		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);

		// No individual, abort
		if (speciesRoot == null) {
			return;
		}

		IIndividual individual = speciesRoot.getMember(specimen);

		// Analyze if necessary
		if (individual != null && !individual.isAnalyzed()) {
			final boolean requiresEnergy = ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE);
			ItemStack energyStack = alyzerInventory.getStackInSlot(InventoryDatabaseAnalyzer.SLOT_ENERGY);
			if (requiresEnergy && !ItemInventoryAlyzer.isAlyzingFuel(energyStack)) {
				return;
			}

			if (individual.analyze()) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());
				breedingTracker.registerSpecies(individual.getGenome().getPrimary());
				breedingTracker.registerSpecies(individual.getGenome().getSecondary());

				NBTTagCompound nbttagcompound = new NBTTagCompound();
				individual.writeToNBT(nbttagcompound);
				specimen = specimen.copy();
				specimen.setTagCompound(nbttagcompound);

				if (requiresEnergy) {
					// Decrease energy
					alyzerInventory.decrStackSize(InventoryDatabaseAnalyzer.SLOT_ENERGY, 1);
				}
			}
			specimenSlot.putStack(specimen);
		}
		return;
	}
}
