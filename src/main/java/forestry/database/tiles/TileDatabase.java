package forestry.database.tiles;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.tiles.TileBase;
import forestry.core.utils.GeneticsUtil;
import forestry.database.gui.ContainerDatabase;
import forestry.database.gui.GuiDatabase;
import forestry.database.inventory.InventoryDatabase;
import forestry.database.inventory.InventoryDatabaseAnalyzer;
import forestry.modules.ForestryModuleUids;

public class TileDatabase extends TileBase implements IOwnedTile {

	//The last the selected slot on the client side.
	@SideOnly(Side.CLIENT)
	public int selectedSlot;

	private final OwnerHandler ownerHandler = new OwnerHandler();
	public InventoryDatabaseAnalyzer analyzerInventory;

	public TileDatabase() {
		setInternalInventory(new InventoryDatabase(this));
		analyzerInventory = new InventoryDatabaseAnalyzer(this);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			selectedSlot = -1;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiDatabase(this, player);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerDatabase(this, player.inventory);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		ownerHandler.writeToNBT(nbttagcompound);
		analyzerInventory.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ownerHandler.readFromNBT(nbttagcompound);
		analyzerInventory.readFromNBT(nbttagcompound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound updateTag = super.getUpdateTag();
		ownerHandler.writeToNBT(updateTag);
		return updateTag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		ownerHandler.readFromNBT(tag);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	//Called by the container on the server side
	public ItemStack analyzeSpecimen(int selectedDatabaseSlot) {
		if (selectedDatabaseSlot < 0) {
			return ItemStack.EMPTY;
		}
		ItemStack specimen = getStackInSlot(selectedDatabaseSlot);
		if (specimen.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack convertedSpecimen = GeneticsUtil.convertToGeneticEquivalent(specimen);
		if (!ItemStack.areItemStacksEqual(specimen, convertedSpecimen)) {
			setInventorySlotContents(selectedDatabaseSlot, convertedSpecimen);
			specimen = convertedSpecimen;
		}

		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);

		// No individual, abort
		if (speciesRoot == null) {
			return specimen;
		}

		IIndividual individual = speciesRoot.getMember(specimen);

		// Analyze if necessary
		if (individual != null && !individual.isAnalyzed()) {
			final boolean requiresEnergy = ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.APICULTURE));
			if (requiresEnergy) {
				// Requires energy
				if (!analyzerInventory.isAlyzingFuel(analyzerInventory.getStackInSlot(InventoryDatabaseAnalyzer.SLOT_ENERGY))) {
					return specimen;
				}
			}

			if (individual.analyze()) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(world, ownerHandler.getOwner());
				breedingTracker.registerSpecies(individual.getGenome().getPrimary());
				breedingTracker.registerSpecies(individual.getGenome().getSecondary());

				NBTTagCompound nbttagcompound = new NBTTagCompound();
				individual.writeToNBT(nbttagcompound);
				specimen.setTagCompound(nbttagcompound);

				if (requiresEnergy) {
					// Decrease energy
					analyzerInventory.decrStackSize(InventoryDatabaseAnalyzer.SLOT_ENERGY, 1);
				}
			}
			setInventorySlotContents(selectedDatabaseSlot, specimen);
		}
		return specimen;
	}
}
