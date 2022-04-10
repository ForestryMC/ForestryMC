package forestry.farming.multiblock;

import java.util.Optional;
import java.util.Stack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import forestry.core.config.Preference;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterRestricted;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.SlotUtil;

/**
 * The basic class for the two plantation inventories. (Multiblock Farm / Cultivators)
 * <p>
 * It contains the biggest part of the logic for the inventories like item validation and fertilizer consumption.
 */
public abstract class InventoryPlantation<H extends ILiquidTankTile & IFarmHousing> extends InventoryAdapterRestricted implements IFarmInventoryInternal {
	/**
	 * Config value which modifies the usage of fertilizer.
	 */
	private static final int FERTILIZER_MODIFIER = Preference.FARM_FERTILIZER_MODIFIER;

	/**
	 * Farm logic object
	 */
	protected final H housing;
	/**
	 * Inventory slot config
	 */
	protected final InventoryConfig config;

	/**
	 * The part of the inventory that contains the resources.
	 */
	protected final IInventory resourcesInventory;
	/**
	 * The part of the inventory that contains the germlings.
	 */
	protected final IInventory germlingsInventory;
	/**
	 * The part of the inventory that contains the output resources.
	 */
	protected final IInventory productInventory;
	/**
	 * The part of the inventory that contains the fertilizer.
	 */
	protected final IInventory fertilizerInventory;

	/**
	 * Creates a inventory instance.
	 *
	 * @param housing Logic object of the farm that owns this inventory
	 * @param config  Helper object that defines the slots of the inventory
	 */
	public InventoryPlantation(H housing, InventoryConfig config) {
		super(config.count, "Items");
		this.housing = housing;
		this.config = config;

		this.resourcesInventory = new InventoryMapper(this, config.resourcesStart, config.resourcesCount);
		this.germlingsInventory = new InventoryMapper(this, config.germlingsStart, config.germlingsCount);
		this.productInventory = new InventoryMapper(this, config.productionStart, config.productionCount);
		this.fertilizerInventory = new InventoryMapper(this, config.fertilizerStart, config.fertilizerCount);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (SlotUtil.isSlotInRange(slotIndex, config.fertilizerStart, config.fertilizerCount)) {
			return acceptsAsFertilizer(itemStack);
		} else if (SlotUtil.isSlotInRange(slotIndex, config.germlingsStart, config.germlingsCount)) {
			return acceptsAsSeedling(itemStack);
		} else if (SlotUtil.isSlotInRange(slotIndex, config.resourcesStart, config.productionCount)) {
			return acceptsAsResource(itemStack);
		} else if (SlotUtil.isSlotInRange(slotIndex, config.canStart, config.canCount)) {
			Optional<FluidStack> fluid = FluidUtil.getFluidContained(itemStack);
			return fluid.map(f -> housing.getTankManager().canFillFluidType(f)).orElse(false);
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack stack, Direction side) {
		return SlotUtil.isSlotInRange(slotIndex, config.productionStart, config.productionCount);
	}

	@Override
	public boolean hasResources(NonNullList<ItemStack> resources) {
		return InventoryUtil.contains(resourcesInventory, resources);
	}

	@Override
	public void removeResources(NonNullList<ItemStack> resources) {
		InventoryUtil.removeSets(resourcesInventory, 1, resources, null, false, false, true);
	}

	@Override
	public boolean acceptsAsSeedling(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (IFarmLogic logic : housing.getFarmLogics()) {
			if (logic.getProperties().isAcceptedSeedling(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean acceptsAsResource(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (IFarmLogic logic : housing.getFarmLogics()) {
			if (logic.getProperties().isAcceptedResource(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean acceptsAsFertilizer(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		return ForestryAPI.farmRegistry.getFertilizeValue(stack) > 0;
	}

	@Override
	public IInventory getProductInventory() {
		return productInventory;
	}

	@Override
	public IInventory getGermlingsInventory() {
		return germlingsInventory;
	}

	@Override
	public IInventory getResourcesInventory() {
		return resourcesInventory;
	}

	@Override
	public IInventory getFertilizerInventory() {
		return fertilizerInventory;
	}

	public void drainCan(TankManager tankManager) {
		FluidHelper.drainContainers(tankManager, this, config.canStart);
	}

	/**
	 * Plants a germling / sapling at the given location.
	 *
	 * @param germling The germling to be placed
	 * @param player   The placer of the germling. Most likely a fake player
	 * @param pos      The position the germling should be placed on
	 * @return True if the germling was placed, false otherwise
	 */
	public abstract boolean plantGermling(IFarmable germling, PlayerEntity player, BlockPos pos);

	public void stowProducts(Iterable<ItemStack> harvested, Stack<ItemStack> pendingProduce) {
		for (ItemStack harvest : harvested) {
			int added = InventoryUtil.addStack(productInventory, harvest, true);
			harvest.shrink(added);
			if (!harvest.isEmpty()) {
				pendingProduce.push(harvest);
			}
		}
	}

	public boolean tryAddPendingProduce(Stack<ItemStack> pendingProduce) {
		IInventory productInventory = getProductInventory();

		ItemStack next = pendingProduce.peek();
		boolean added = InventoryUtil.tryAddStack(productInventory, next, true, true);

		if (added) {
			pendingProduce.pop();
		}

		return added;
	}

	public int getFertilizerValue() {
		ItemStack fertilizerStack = getItem(config.fertilizerStart);
		if (fertilizerStack.isEmpty()) {
			return 0;
		}

		int fertilizerValue = ForestryAPI.farmRegistry.getFertilizeValue(fertilizerStack);
		if (fertilizerValue > 0) {
			return fertilizerValue * FERTILIZER_MODIFIER;
		}
		return 0;
	}

	public boolean useFertilizer() {
		ItemStack fertilizer = getItem(config.fertilizerStart);
		if (acceptsAsFertilizer(fertilizer)) {
			removeItem(config.fertilizerStart, 1);
			return true;
		}
		return false;
	}

	/**
	 * Describes the slots if a farm inventory
	 */
	public static class InventoryConfig {
		/* Slots that contain the input resources (dirt, sand, ...) */
		public final int resourcesStart;
		public final int resourcesCount;
		/* Slots that contain the germlings (saplings, cactus blocks, ...) */
		public final int germlingsStart;
		public final int germlingsCount;
		/* Slots that contain the output of the farm / the products (wood, saplings, ...) */
		public final int productionStart;
		public final int productionCount;
		/* Slots that contain the fertilizer */
		public final int fertilizerStart;
		public final int fertilizerCount;
		/* Slots that contain the fluid containers for the water of the farm */
		public final int canStart;
		public final int canCount;
		/* Amount of slots in this config*/
		public final int count;

		public InventoryConfig(
				int resourcesStart, int resourcesCount,
				int germlingsStart, int germlingsCount,
				int productionStart, int productionCount,
				int fertilizerStart, int fertilizerCount,
				int canStart, int canCount
		) {
			this.resourcesStart = resourcesStart;
			this.resourcesCount = resourcesCount;
			this.germlingsStart = germlingsStart;
			this.germlingsCount = germlingsCount;
			this.productionStart = productionStart;
			this.productionCount = productionCount;
			this.fertilizerStart = fertilizerStart;
			this.fertilizerCount = fertilizerCount;
			this.canStart = canStart;
			this.canCount = canCount;
			this.count = resourcesCount + germlingsCount + productionCount + fertilizerCount + canCount;
		}
	}
}
