package forestry.cultivation.tiles;

import com.google.common.base.Preconditions;
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
import forestry.core.tiles.IClimatised;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class TilePlanter extends TilePowered implements IFarmHousingInternal, IClimatised, ILiquidTankTile, IOwnedTile, IStreamableGui {
    private final InventoryPlanter inventory;
    private final OwnerHandler ownerHandler = new OwnerHandler();
    private final FarmManager manager;

    private int platformHeight = -1;
    private BlockPlanter.Mode mode;
    private final IFarmProperties properties;
    @Nullable
    private IFarmLogic logic;
    @Nullable
    private Vector3i offset;
    @Nullable
    private Vector3i area;

    public void setManual(BlockPlanter.Mode mode) {
        this.mode = mode;
        logic = properties.getLogic(this.mode == BlockPlanter.Mode.MANUAL);
    }

    protected TilePlanter(TileEntityType type, String identifier) {
        super(type, 150, 1500);
        this.properties = Preconditions.checkNotNull(FarmRegistry.getInstance().getProperties(identifier));
        mode = BlockPlanter.Mode.MANAGED;
        setInternalInventory(inventory = new InventoryPlanter(this));
        this.manager = new FarmManager(this);
        setEnergyPerWorkCycle(10);
        setTicksPerWorkCycle(2);
    }

    @Override
    public ITextComponent getDisplayName() {
        String name = getBlockType(BlockTypePlanter.ARBORETUM).getString();
        return new TranslationTextComponent("block.forestry.planter." + (mode.getString()), new TranslationTextComponent("block.forestry." + name));
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        this.platformHeight = posIn.getY() - 2;
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
    public CompoundNBT write(CompoundNBT data) {
        data = super.write(data);
        manager.write(data);
        ownerHandler.write(data);
        data.putInt("mode", mode.ordinal());
        return data;
    }

    @Override
    public void read(BlockState state, CompoundNBT data) {
        super.read(state, data);
        manager.read(data);
        ownerHandler.read(data);
        setManual(BlockPlanter.Mode.values()[data.getInt("mode")]);
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        manager.writeData(data);
    }

    @Override
    public void readGuiData(PacketBufferForestry data) throws IOException {
        manager.readData(data);
    }

    @Override
    public void setUpFarmlandTargets(Map<FarmDirection, List<FarmTarget>> targets) {
        BlockPos targetStart = getCoords();
        BlockPos minPos = pos;
        BlockPos maxPos = pos;
        int size = 1;
        int extend = Config.planterExtend;

        if (Config.ringFarms) {
            int ringSize = Config.ringSize;
            minPos = pos.add(-ringSize, 0, -ringSize);
            maxPos = pos.add(ringSize, 0, ringSize);
            size = 1 + ringSize * 2;
            extend--;
        }

        FarmHelper.createTargets(world, this, targets, targetStart, extend, size, size, minPos, maxPos);
        FarmHelper.setExtents(world, this, targets);
    }

    @Override
    public BlockPos getCoords() {
        return pos;
    }

    @Override
    public BlockPos getTopCoord() {
        return pos;
    }

    @Override
    public Vector3i getArea() {
        if (area == null) {
            int basisArea = 5;
            if (Config.ringFarms) {
                basisArea = basisArea + 1 + Config.ringSize * 2;
            }
            area = new Vector3i(basisArea + Config.planterExtend, 13, basisArea + Config.planterExtend);
        }
        return area;
    }

    @Override
    public Vector3i getOffset() {
        if (offset == null) {
            Vector3i area = getArea();
            offset = new Vector3i(-area.getX() / 2, -2, -area.getZ() / 2);
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
    public boolean plantGermling(IFarmable farmable, World world, BlockPos pos, FarmDirection direction) {
        PlayerEntity player = PlayerUtil.getFakePlayer(world, getOwnerHandler().getOwner());
        return player != null && inventory.plantGermling(farmable, player, pos, direction);
    }

    @Override
    public boolean isValidPlatform(World world, BlockPos pos) {
        return pos.getY() == platformHeight;
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
    public int getStoredFertilizerScaled(int scale) {
        return manager.getFertilizerManager().getStoredFertilizerScaled(inventory, scale);
    }

    @Override
    public void remove() {
        super.remove();
        manager.clearTargets();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT data = super.getUpdateTag();
        manager.write(data);
        return data;
    }

    protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
        return VectUtil.scale(farmDirection.getFacing().getDirectionVec(), step).add(pos);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
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
        return world.getBiome(coords).getTemperature(coords);
    }

    @Override
    public float getExactHumidity() {
        BlockPos coords = getCoordinates();
        return world.getBiome(coords).getDownfall();
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
        return NonNullList.from(ItemStack.EMPTY, stacks);
    }

    public abstract NonNullList<ItemStack> createGermlingStacks();

    public abstract NonNullList<ItemStack> createResourceStacks();

    public abstract NonNullList<ItemStack> createProductionStacks();

    @Override
    public BlockPos getFarmCorner(FarmDirection direction) {
        return pos.down(2);
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
