package forestry.cultivation.tiles;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.climate.IClimatised;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.core.config.Config;
import forestry.core.fluids.ITankManager;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.VectUtil;
import forestry.cultivation.IFarmHousingInternal;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.gui.ContainerPlanter;
import forestry.cultivation.inventory.InventoryPlanter;
import forestry.farming.FarmHelper;
import forestry.farming.FarmManager;
import forestry.farming.FarmRegistry;
import forestry.farming.FarmTarget;
import forestry.farming.gui.IFarmLedgerDelegate;
import forestry.farming.multiblock.IFarmInventoryInternal;

public abstract class TilePlanter extends TilePowered implements IFarmHousingInternal, IClimatised, ILiquidTankTile, IOwnedTile, IStreamableGui {
	private final InventoryPlanter inventory;
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private final FarmManager manager;

	private BlockPlanter.Mode mode;
	private final IFarmProperties properties;
	@Nullable
	private IFarmLogic logic;
	@Nullable
	private Vec3i offset;
	@Nullable
	private Vec3i area;

	public void setManual(BlockPlanter.Mode mode) {
		this.mode = mode;
		logic = properties.getLogic(this.mode == BlockPlanter.Mode.MANUAL);
	}

	protected TilePlanter(BlockEntityType type, BlockPos pos, BlockState state, String identifier) {
		super(type, pos, state, 150, 1500);
		this.properties = Preconditions.checkNotNull(FarmRegistry.getInstance().getProperties(identifier));
		mode = BlockPlanter.Mode.MANAGED;
		setInternalInventory(inventory = new InventoryPlanter(this));
		this.manager = new FarmManager(this);
		setEnergyPerWorkCycle(10);
		setTicksPerWorkCycle(2);
	}

	@Override
	public Component getDisplayName() {
		String name = getBlockType(BlockTypePlanter.ARBORETUM).getSerializedName();
		return Component.translatable("block.forestry.planter." + (mode.getSerializedName()), Component.translatable("block.forestry." + name));
	}

	@Override
	public boolean hasWork() {
		return true;
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		manager.getHydrationManager().updateServer();

		if (updateOnInterval(20)) {
			inventory.drainCan(manager.getTankManager());
		}
	}

	@Override
	protected boolean workCycle() {
		manager.doWork();
		return false;
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);
		manager.write(data);
		ownerHandler.write(data);
		data.putInt("mode", mode.ordinal());
	}

	@Override
	public void load(CompoundTag data) {
		super.load(data);
		manager.read(data);
		ownerHandler.read(data);
		setManual(BlockPlanter.Mode.values()[data.getInt("mode")]);
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		super.writeGuiData(data);
		manager.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		super.readGuiData(data);
		manager.readData(data);

	}

	@Override
	public void setUpFarmlandTargets(Map<FarmDirection, List<FarmTarget>> targets) {
		BlockPos targetStart = getCoords();
		BlockPos minPos = worldPosition;
		BlockPos maxPos = worldPosition;
		int size = 1;
		int extend = Config.planterExtend;

		if (Config.ringFarms) {
			int ringSize = Config.ringSize;
			minPos = worldPosition.offset(-ringSize, 0, -ringSize);
			maxPos = worldPosition.offset(ringSize, 0, ringSize);
			size = 1 + ringSize * 2;
			extend--;
		}

		FarmHelper.createTargets(level, this, targets, targetStart, extend, size, size, minPos, maxPos);
		FarmHelper.setExtents(level, this, targets);
	}

	@Override
	public BlockPos getCoords() {
		return worldPosition;
	}

	@Override
	public BlockPos getTopCoord() {
		return worldPosition;
	}

	@Override
	public Vec3i getArea() {
		if (area == null) {
			int basisArea = 5;
			if (Config.ringFarms) {
				basisArea = basisArea + 1 + Config.ringSize * 2;
			}
			area = new Vec3i(basisArea + Config.planterExtend, 13, basisArea + Config.planterExtend);
		}
		return area;
	}

	@Override
	public Vec3i getOffset() {
		if (offset == null) {
			Vec3i area = getArea();
			offset = new Vec3i(-area.getX() / 2, -2, -area.getZ() / 2);
		}
		return offset;
	}

	@Override
	public boolean doWork() {
		return false;
	}

	@Override
	public boolean hasLiquid(FluidStack liquid) {
		FluidStack drained = manager.getResourceTank().drainInternal(liquid, IFluidHandler.FluidAction.SIMULATE);
		return liquid.isFluidStackIdentical(drained);
	}

	@Override
	public void removeLiquid(FluidStack liquid) {
		manager.getResourceTank().drain(liquid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public boolean plantGermling(IFarmable farmable, Level world, BlockPos pos, FarmDirection direction) {
		Player player = PlayerUtil.getFakePlayer(world, getOwnerHandler().getOwner());
		return player != null && inventory.plantGermling(farmable, player, pos, direction);
	}

	@Override
	public boolean isValidPlatform(Level world, BlockPos pos) {
		return pos.getY() == getBlockPos().getY() - 2;
	}

	@Override
	public boolean isSquare() {
		return true;
	}

	@Override
	public boolean canPlantSoil(boolean manual) {
		return mode == BlockPlanter.Mode.MANAGED;
	}

	@Override
	public IFarmInventoryInternal getFarmInventory() {
		return inventory;
	}

	@Override
	public void addPendingProduct(ItemStack stack) {
		manager.addPendingProduct(stack);
	}

	@Override
	public void setFarmLogic(FarmDirection direction, IFarmLogic logic) {
	}

	@Override
	public void resetFarmLogic(FarmDirection direction) {
	}

	@Override
	public IFarmLogic getFarmLogic(FarmDirection direction) {
		return getFarmLogic();
	}

	public IFarmLogic getFarmLogic() {
		return logic;
	}

	@Override
	public Collection<IFarmLogic> getFarmLogics() {
		return Collections.singleton(logic);
	}

	@Override
	public int getStoredFertilizerScaled(int scale) {
		return manager.getFertilizerManager().getStoredFertilizerScaled(inventory, scale);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		manager.clearTargets();
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag data = super.getUpdateTag();
		manager.write(data);
		return data;
	}

	protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return VectUtil.scale(farmDirection.getFacing().getNormal(), step).offset(pos);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerPlanter(windowId, inv, this);
	}

	public IFarmLedgerDelegate getFarmLedgerDelegate() {
		return manager.getHydrationManager();
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		BlockPos coords = getCoordinates();
		return 0; // level.getBiome(coords).getTemperature(coords);
	}

	@Override
	public float getExactHumidity() {
		BlockPos coords = getCoordinates();
		Level level = Objects.requireNonNull(this.level);
		return level.getBiome(coords).value().getDownfall();
	}

	@Override
	public ITankManager getTankManager() {
		return manager.getTankManager();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(this::getTankManager).cast();
		}
		return super.getCapability(capability, facing);
	}

	protected NonNullList<ItemStack> createList(ItemStack... stacks) {
		return NonNullList.of(ItemStack.EMPTY, stacks);
	}

	public abstract NonNullList<ItemStack> createGermlingStacks();

	public abstract NonNullList<ItemStack> createResourceStacks();

	public abstract NonNullList<ItemStack> createProductionStacks();

	@Override
	public BlockPos getFarmCorner(FarmDirection direction) {
		return worldPosition.below(2);
	}

	@Override
	public int getExtents(FarmDirection direction, BlockPos pos) {
		return manager.getExtents(direction, pos);
	}

	@Override
	public void setExtents(FarmDirection direction, BlockPos pos, int extend) {
		manager.setExtents(direction, pos, extend);
	}

	@Override
	public void cleanExtents(FarmDirection direction) {
		manager.cleanExtents(direction);
	}
}
