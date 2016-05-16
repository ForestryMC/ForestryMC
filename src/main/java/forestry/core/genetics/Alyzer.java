package forestry.core.genetics;

import java.util.HashMap;
import java.util.Map;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlyzer;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IGuiAlyzer;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public abstract class Alyzer<II extends IIndividual, T extends ISpeciesType, G extends IGuiAlyzer> implements IAlyzer<II, T, G, ItemInventoryAlyzer> {

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();
	private final ISpeciesRoot speciesRoot;
	
	public Alyzer(ISpeciesRoot speciesRoot) {
		this.speciesRoot = speciesRoot;
	}
	
	@Override
	public boolean canSlotAccept(ItemInventoryAlyzer inventory, int slotIndex, ItemStack itemStack) {
		if (slotIndex == ItemInventoryAlyzer.SLOT_ENERGY) {
			if(inventory.hasSpecimen()){
				ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(inventory.getSpecimen());
				return speciesRoot.getAlyzer().isAlyzingFuel(itemStack);
			}
			return false;
		}
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		if (speciesRoot == null || !speciesRoot.isMember(itemStack)) {
			return false;
		}

		// only allow one slot to be used at a time
		if (inventory.hasSpecimen() && inventory.getStackInSlot(slotIndex) == null) {
			return false;
		}

		if (slotIndex == ItemInventoryAlyzer.SLOT_SPECIMEN) {
			return true;
		}

		IIndividual individual = speciesRoot.getMember(itemStack);
		return individual.isAnalyzed();
	}

	@Override
	public void onSlotClick(ItemInventoryAlyzer inventory, int slotIndex, EntityPlayer player) {
		// Source slot to analyze empty
		ItemStack specimen = inventory.getStackInSlot(ItemInventoryAlyzer.SLOT_SPECIMEN);
		if (specimen == null) {
			return;
		}

		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);
		
		// No individual, abort
		if(speciesRoot == null){
			return;
		}
		
		IIndividual individual = speciesRoot.getMember(specimen);

		// Analyze if necessary
		if (!individual.isAnalyzed()) {

			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
				// Requires energy
				if (!speciesRoot.getAlyzer().isAlyzingFuel(inventory.getStackInSlot(ItemInventoryAlyzer.SLOT_ENERGY))) {
					return;
				}
			}

			individual.analyze();
			if (player != null) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
				breedingTracker.registerSpecies(individual.getGenome().getPrimary());
				breedingTracker.registerSpecies(individual.getGenome().getSecondary());
			}

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			individual.writeToNBT(nbttagcompound);
			specimen.setTagCompound(nbttagcompound);

			// Decrease energy
			inventory.decrStackSize(ItemInventoryAlyzer.SLOT_ENERGY, 1);
		}

		inventory.setInventorySlotContents(ItemInventoryAlyzer.SLOT_ANALYZE_1, specimen);
		inventory.setInventorySlotContents(ItemInventoryAlyzer.SLOT_SPECIMEN, null);
		PluginCore.items.portableAlyzer.openGui(player);
	}
	
	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return speciesRoot;
	}

	@Override
	public Map<String, ItemStack> getIconStacks() {
		return iconStacks;
	}

	@Override
	public ResourceLocation getGuiTexture() {
		return new ResourceLocation(Constants.RESOURCE_ID, Constants.TEXTURE_PATH_GUI + "/portablealyzer.png");
	}

	@Override
	public boolean isAlyzingFuel(ItemStack itemstack) {
		if (itemstack == null || itemstack.stackSize <= 0) {
			return false;
		}

		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems == null) {
			return false;
		}

		Item item = itemstack.getItem();
		return beeItems.honeyDrop == item || beeItems.honeydew == item;
	}

}
