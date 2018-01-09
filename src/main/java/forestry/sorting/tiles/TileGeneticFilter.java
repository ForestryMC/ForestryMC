package forestry.sorting.tiles;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.sorting.AlleleFilter;
import forestry.sorting.DefaultFilterRule;
import forestry.sorting.FilterData;
import forestry.sorting.gui.ContainerGeneticFilter;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.inventory.InventoryFilter;
import forestry.sorting.inventory.ItemHandlerFilter;

public class TileGeneticFilter extends TileForestry implements IStreamableGui {
	private static final int TRANSFER_DELAY = 5;

	private IFilterRule[] filterRules = new IFilterRule[6];
	private AlleleFilter[][] genomeFilter = new AlleleFilter[6][3];
	private final AdjacentInventoryCache inventoryCache;

	public TileGeneticFilter() {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getDefaultRule();
		}
		this.inventoryCache = new AdjacentInventoryCache(this, getTileCache());
		setInternalInventory(new InventoryFilter(this));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		for (int i = 0; i < filterRules.length; i++) {
			data.setString("TypeFilter" + i, filterRules[i].getUID());
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					continue;
				}
				if (filter.activeAllele != null) {
					data.setString("GenomeFilterS" + i + "-" + j + "-" + 0, filter.activeAllele.getUID());
				}
				if (filter.inactiveAllele != null) {
					data.setString("GenomeFilterS" + i + "-" + j + "-" + 1, filter.inactiveAllele.getUID());
				}
			}
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRuleOrDefault(data.getString("TypeFilter" + i));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.hasKey("GenomeFilterS" + i + "-" + j + "-" + 0)) {
					filter.activeAllele = AlleleManager.alleleRegistry.getAllele(data.getString("GenomeFilterS" + i + "-" + j + "-" + 0));
				}
				if (data.hasKey("GenomeFilterS" + i + "-" + j + "-" + 1)) {
					filter.inactiveAllele = AlleleManager.alleleRegistry.getAllele(data.getString("GenomeFilterS" + i + "-" + j + "-" + 1));
				}
				genomeFilter[i][j] = filter;
			}
		}
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		for (int i = 0; i < filterRules.length; i++) {
			data.writeShort(AlleleManager.filterRegistry.getId(filterRules[i]));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					data.writeBoolean(false);
					data.writeBoolean(false);
					continue;
				}
				if (filter.activeAllele != null) {
					data.writeBoolean(true);
					data.writeString(filter.activeAllele.getUID());
				} else {
					data.writeBoolean(false);
				}
				if (filter.inactiveAllele != null) {
					data.writeBoolean(true);
					data.writeString(filter.inactiveAllele.getUID());
				} else {
					data.writeBoolean(false);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRule(data.readShort());
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.readBoolean()) {
					filter.activeAllele = AlleleManager.alleleRegistry.getAllele(data.readString());
				}
				if (data.readBoolean()) {
					filter.inactiveAllele = AlleleManager.alleleRegistry.getAllele(data.readString());
				}
				genomeFilter[i][j] = filter;
			}
		}
	}

	public void sendToPlayers(WorldServer server, EntityPlayer entityPlayer) {
		for (EntityPlayer player : server.playerEntities) {
			if (player != entityPlayer && player.openContainer instanceof ContainerGeneticFilter) {
				if (((ContainerGeneticFilter) entityPlayer.openContainer).hasSameTile((ContainerGeneticFilter) player.openContainer)) {
					((ContainerGeneticFilter) player.openContainer).setGuiNeedsUpdate(true);
				}
			}
		}
	}

	@Override
	protected void updateServerSide() {
		if (updateOnInterval(TRANSFER_DELAY)) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				ItemStack stack = getStackInSlot(facing.getIndex());
				if (stack.isEmpty()) {
					continue;
				}
				ItemStack transferredStack = transferItem(stack, facing);
				int remaining = stack.getCount() - transferredStack.getCount();
				if (remaining > 0) {
					stack = stack.copy();
					stack.setCount(remaining);
					ItemStackUtil.dropItemStackAsEntity(stack.copy(), world, pos.getX(), pos.getY() + 0.5F, pos.getZ());
				}
				setInventorySlotContents(facing.getIndex(), ItemStack.EMPTY);
			}
		}
	}

	public boolean isConnected(EnumFacing facing) {
		if (inventoryCache.getAdjacentInventory(facing) != null) {
			return true;
		}
		TileEntity tileEntity = world.getTileEntity(pos.offset(facing));
		return TileUtil.getInventoryFromTile(tileEntity, facing.getOpposite()) != null;
	}

	private ItemStack transferItem(ItemStack itemStack, EnumFacing facing) {
		IItemHandler itemHandler = inventoryCache.getAdjacentInventory(facing);
		if (itemHandler == null) {
			return ItemStack.EMPTY;
		}
		ItemStack transferredStack = ItemHandlerHelper.insertItemStacked(itemHandler, itemStack.copy(), true);
		if (transferredStack.getCount() == itemStack.getCount()) {
			return ItemStack.EMPTY;
		}
		transferredStack = ItemHandlerHelper.insertItemStacked(itemHandler, itemStack.copy(), false);
		if (transferredStack.isEmpty()) {
			return itemStack;
		}
		ItemStack copy = itemStack.copy();
		copy.setCount(itemStack.getCount() - transferredStack.getCount());
		return copy;
	}

	public Collection<EnumFacing> getValidDirections(ItemStack itemStack, EnumFacing from) {
		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		IIndividual individual = null;
		ISpeciesType type = null;
		if (root != null) {
			individual = root.getMember(itemStack);
			type = root.getType(itemStack);
		}
		IFilterData filterData = new FilterData(root, individual, type);
		List<EnumFacing> validFacings = new LinkedList<>();
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (facing == from) {
				continue;
			}
			if (isValidFacing(facing, itemStack, filterData)) {
				validFacings.add(facing);
			}
		}
		return validFacings;
	}

	private boolean isValidFacing(EnumFacing facing, ItemStack itemStack, IFilterData filterData) {
		if (inventoryCache.getAdjacentInventory(facing) == null) {
			return false;
		}
		IFilterRule rule = getRule(facing);
		if (rule == DefaultFilterRule.CLOSED) {
			return false;
		}
		if (rule == DefaultFilterRule.ITEM && !filterData.isPresent()) {
			return true;
		}
		String requiredRoot = rule.getRootUID();
		if (requiredRoot != null && (!filterData.isPresent() || !filterData.getRoot().getUID().equals(requiredRoot))) {
			return false;
		}
		if (rule == DefaultFilterRule.ANYTHING || rule.isValid(itemStack, filterData)) {
			if (filterData.isPresent()) {
				IIndividual ind = filterData.getIndividual();
				IGenome genome = ind.getGenome();
				IAllele active = genome.getPrimary();
				IAllele inactive = genome.getSecondary();
				if (!isValidGenome(facing, active.getUID(), inactive.getUID())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isValidGenome(EnumFacing orientation, String activeUID, String inactiveUID) {
		AlleleFilter[] directionFilters = genomeFilter[orientation.ordinal()];

		if (directionFilters == null) {
			return true;
		}

		boolean foundFilter = false;
		for (int i = 0; i < 3; i++) {
			AlleleFilter filter = directionFilters[i];
			if (filter != null && !filter.isEmpty()) {
				foundFilter = true;
				if (!filter.isEmpty() && filter.isValid(activeUID, inactiveUID)) {
					return true;
				}
			}
		}
		return !foundFilter;
	}

	public IFilterRule getRule(EnumFacing facing) {
		return filterRules[facing.ordinal()];
	}

	public boolean setRule(EnumFacing facing, IFilterRule rule) {
		if (filterRules[facing.ordinal()] != rule) {
			filterRules[facing.ordinal()] = rule;
			return true;
		}
		return false;
	}

	@Nullable
	public AlleleFilter getGenomeFilter(EnumFacing facing, int index) {
		return genomeFilter[facing.ordinal()][index];
	}

	public boolean setGenomeFilter(EnumFacing facing, int index, boolean active, @Nullable IAllele allele) {
		AlleleFilter filter = genomeFilter[facing.ordinal()][index];
		if (filter == null) {
			filter = genomeFilter[facing.ordinal()][index] = new AlleleFilter();
		}
		boolean set;
		if (active) {
			set = filter.activeAllele != allele;
			filter.activeAllele = allele;
		} else {
			set = filter.inactiveAllele != allele;
			filter.inactiveAllele = allele;
		}
		return set;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiGeneticFilter(this, player.inventory);
	}

	@Nullable
	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerGeneticFilter(this, player.inventory);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new ItemHandlerFilter(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
}
