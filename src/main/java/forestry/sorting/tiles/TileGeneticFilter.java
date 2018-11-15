package forestry.sorting.tiles;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
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
import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.sorting.FilterData;
import forestry.sorting.FilterLogic;
import forestry.sorting.gui.ContainerGeneticFilter;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.inventory.InventoryFilter;
import forestry.sorting.inventory.ItemHandlerFilter;

public class TileGeneticFilter extends TileForestry implements IStreamableGui, IFilterContainer {
	private static final int TRANSFER_DELAY = 5;

	private final FilterLogic logic;
	private final AdjacentInventoryCache inventoryCache;

	public TileGeneticFilter() {
		this.inventoryCache = new AdjacentInventoryCache(this, getTileCache());
		this.logic = new FilterLogic(this, (logic1, server, player) -> sendToPlayers(server, player));
		setInternalInventory(new InventoryFilter(this));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		data.setTag("Logic", logic.writeToNBT(new NBTTagCompound()));

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		logic.readFromNBT(data.getCompoundTag("Logic"));
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		logic.writeGuiData(data);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void readGuiData(PacketBufferForestry data) {
		logic.readGuiData(data);
	}

	private void sendToPlayers(WorldServer server, EntityPlayer entityPlayer) {
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
		return inventoryCache.getAdjacentInventory(facing) != null && logic.isValid(facing, itemStack, filterData);
	}

	public IFilterLogic getLogic() {
		return logic;
	}

	@Override
	public IInventory getBuffer() {
		return this;
	}

	@Override
	public TileEntity getTileEntity() {
		return this;
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
		if (capability == GeneticCapabilities.FILTER_LOGIC) {
			return GeneticCapabilities.FILTER_LOGIC.cast(logic);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == GeneticCapabilities.FILTER_LOGIC || super.hasCapability(capability, facing);
	}
}
