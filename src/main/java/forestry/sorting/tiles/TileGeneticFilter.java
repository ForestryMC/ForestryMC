package forestry.sorting.tiles;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IRootDefinition;

import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.sorting.FilterData;
import forestry.sorting.FilterLogic;
import forestry.sorting.ModuleSorting;
import forestry.sorting.gui.ContainerGeneticFilter;
import forestry.sorting.inventory.InventoryFilter;
import forestry.sorting.inventory.ItemHandlerFilter;

public class TileGeneticFilter extends TileForestry implements IStreamableGui, IFilterContainer {
	private static final int TRANSFER_DELAY = 5;

	private final FilterLogic logic;
	private final AdjacentInventoryCache inventoryCache;

	public TileGeneticFilter() {
		super(ModuleSorting.getTiles().GENETIC_FILTER);
		this.inventoryCache = new AdjacentInventoryCache(this, getTileCache());
		this.logic = new FilterLogic(this, (logic1, server, player) -> sendToPlayers(server, player));
		setInternalInventory(new InventoryFilter(this));
	}

	@Override
	public CompoundNBT write(CompoundNBT data) {
		super.write(data);

		data.put("Logic", logic.write(new CompoundNBT()));

		return data;
	}

	@Override
	public void read(CompoundNBT data) {
		super.read(data);

		logic.read(data.getCompound("Logic"));
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		logic.writeGuiData(data);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void readGuiData(PacketBufferForestry data) {
		logic.readGuiData(data);
	}

	private void sendToPlayers(ServerWorld server, PlayerEntity PlayerEntity) {
		for (PlayerEntity player : server.getPlayers()) {
			if (player != PlayerEntity && player.openContainer instanceof ContainerGeneticFilter) {
				if (((ContainerGeneticFilter) PlayerEntity.openContainer).hasSameTile((ContainerGeneticFilter) player.openContainer)) {
					((ContainerGeneticFilter) player.openContainer).setGuiNeedsUpdate(true);
				}
			}
		}
	}

	@Override
	protected void updateServerSide() {
		if (updateOnInterval(TRANSFER_DELAY)) {
			for (Direction facing : Direction.VALUES) {
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

	public boolean isConnected(Direction facing) {
		if (inventoryCache.getAdjacentInventory(facing) != null) {
			return true;
		}
		TileEntity tileEntity = world.getTileEntity(pos.offset(facing));
		return TileUtil.getInventoryFromTile(tileEntity, facing.getOpposite()) != null;
	}

	private ItemStack transferItem(ItemStack itemStack, Direction facing) {
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

	public Collection<Direction> getValidDirections(ItemStack itemStack, Direction from) {
		IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(itemStack);
		IIndividual individual = null;
		IOrganismType type = null;
		if (definition.isRootPresent()) {
			IForestrySpeciesRoot<IIndividual> root = definition.get();
			individual = root.create(itemStack).orElse(null);
			type = root.getTypes().getType(itemStack).orElse(null);
		}
		IFilterData filterData = new FilterData(definition, individual, type);
		List<Direction> validFacings = new LinkedList<>();
		for (Direction facing : Direction.VALUES) {
			if (facing == from) {
				continue;
			}
			if (isValidFacing(facing, itemStack, filterData)) {
				validFacings.add(facing);
			}
		}
		return validFacings;
	}

	private boolean isValidFacing(Direction facing, ItemStack itemStack, IFilterData filterData) {
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
	public TileGeneticFilter getTileEntity() {
		return this;
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerGeneticFilter(windowId, player.inventory, this);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) {
			return LazyOptional.of(() -> new ItemHandlerFilter(this, facing)).cast();
		} else if (capability == GeneticCapabilities.FILTER_LOGIC) {
			return LazyOptional.of(() -> logic).cast();
		}
		return super.getCapability(capability, facing);
	}
}
