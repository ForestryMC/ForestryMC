package forestry.sorting.tiles;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.filter.IFilterData;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.sorting.FilterData;
import forestry.sorting.FilterLogic;
import forestry.sorting.features.SortingTiles;
import forestry.sorting.gui.ContainerGeneticFilter;
import forestry.sorting.inventory.InventoryFilter;
import forestry.sorting.inventory.ItemHandlerFilter;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IRootDefinition;
import genetics.utils.RootUtils;

public class TileGeneticFilter extends TileForestry implements IStreamableGui, IFilterContainer {
	private static final int TRANSFER_DELAY = 5;

	private final FilterLogic logic;
	private final AdjacentInventoryCache inventoryCache;

	public TileGeneticFilter(BlockPos pos, BlockState state) {
		super(SortingTiles.GENETIC_FILTER.tileType(), pos, state);
		this.inventoryCache = new AdjacentInventoryCache(this, getTileCache());
		this.logic = new FilterLogic(this, (logic1, server, player) -> sendToPlayers(server, player));
		setInternalInventory(new InventoryFilter(this));
	}

	@Override
	public CompoundTag save(CompoundTag data) {
		super.save(data);

		data.put("Logic", logic.write(new CompoundTag()));

		return data;
	}

	@Override
	public void load(BlockState state, CompoundTag data) {
		super.load(state, data);

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

	private void sendToPlayers(ServerLevel server, Player PlayerEntity) {
		for (Player player : server.players()) {
			if (player != PlayerEntity && player.containerMenu instanceof ContainerGeneticFilter) {
				if (((ContainerGeneticFilter) PlayerEntity.containerMenu).hasSameTile((ContainerGeneticFilter) player.containerMenu)) {
					((ContainerGeneticFilter) player.containerMenu).setGuiNeedsUpdate(true);
				}
			}
		}
	}

	@Override
	protected void updateServerSide() {
		if (updateOnInterval(TRANSFER_DELAY)) {
			for (Direction facing : Direction.VALUES) {
				ItemStack stack = getItem(facing.get3DDataValue());
				if (stack.isEmpty()) {
					continue;
				}
				ItemStack transferredStack = transferItem(stack, facing);
				int remaining = stack.getCount() - transferredStack.getCount();
				if (remaining > 0) {
					stack = stack.copy();
					stack.setCount(remaining);
					ItemStackUtil.dropItemStackAsEntity(stack.copy(), level, worldPosition.getX(), worldPosition.getY() + 0.5F, worldPosition.getZ());
				}
				setItem(facing.get3DDataValue(), ItemStack.EMPTY);
			}
		}
	}

	public boolean isConnected(Direction facing) {
		if (inventoryCache.getAdjacentInventory(facing) != null) {
			return true;
		}
		BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(facing));
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
		IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = RootUtils.getRoot(itemStack);
		IIndividual individual = null;
		IOrganismType type = null;
		if (definition.isPresent()) {
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
	public Container getBuffer() {
		return this;
	}

	@Override
	public TileGeneticFilter getTileEntity() {
		return this;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerGeneticFilter(windowId, player.getInventory(), this);
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
