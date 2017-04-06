package forestry.apiculture.modules.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;

import de.nedelosk.modularmachines.api.modular.handlers.IModularHandlerTileEntity;
import de.nedelosk.modularmachines.api.modules.handlers.IModuleContentHandler;
import de.nedelosk.modularmachines.api.modules.handlers.IModulePage;
import de.nedelosk.modularmachines.api.modules.handlers.inventory.IModuleInventory;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.modules.ModuleBeeHouse;
import forestry.apiculture.modules.pages.FrameHousingPage;
import forestry.core.errors.ErrorLogic;
import forestry.core.tiles.IClimatised;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.INBTSerializable;

public class BeeHouseHandler implements IModuleContentHandler, IBeeHousing, IClimatised, INBTSerializable<NBTTagCompound> {
	protected final IModuleState<ModuleBeeHouse> state;
	@Nonnull
	protected final IBeeHousingInventory inventory;
	@Nonnull
	protected IBeeModifier beeModifier;
	@Nonnull
	protected IBeeListener beeListener;
	@Nonnull
	protected final IBeekeepingLogic beeLogic;
	private final ErrorLogic errorHandler = new ErrorLogic();
	protected World worldObj;
	protected Biome cachedBiome;
	protected int breedingProgressPercent;
	
	public BeeHouseHandler(@Nonnull IModuleState<ModuleBeeHouse> state, @Nonnull IBeeHousingInventory inventory) {
		this.state = state;
		this.inventory = inventory;
		this.beeModifier = new DefaultBeeModifier();
		this.beeListener = new DefaultBeeListener();
		this.worldObj = state.getModular().getHandler().getWorld();
		this.beeLogic = new ModuleBeekeepingLogic(this);
	}
	
	public void init(IBeeModifier beeModifier, IBeeListener beeListener){
		this.beeModifier = beeModifier;
		this.beeListener = beeListener;
	}
	
	@Override
	public BlockPos getCoordinates() {
		if(state.getModular().getHandler() instanceof IModularHandlerTileEntity){
			return ((IModularHandlerTileEntity)state.getModular().getHandler()).getPos();
		}
		return null;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorHandler;
	}

	@Override
	public Biome getBiome() {
		if (cachedBiome == null) {
			cachedBiome = getWorldObj().getBiome(getCoordinates());
		}
		return cachedBiome;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), getWorldObj(), getCoordinates());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return ForestryAPI.climateManager.getTemperature(worldObj, getCoordinates());
	}

	@Override
	public float getExactHumidity() {
		return ForestryAPI.climateManager.getHumidity(worldObj, getCoordinates());
	}
	
	public int getHealthScaled(int i) {
		return breedingProgressPercent * i / 100;
	}
	
	public void setBreedingProgressPercent(int breedingProgressPercent) {
		this.breedingProgressPercent = breedingProgressPercent;
	}
	
	@Override
	public World getWorldObj() {
		if(worldObj == null){
			worldObj = state.getModular().getHandler().getWorld();
		}
		return worldObj;
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		if(state.getModule().isApiary){
			List<IBeeModifier> beeModifiers = new ArrayList<>();

			beeModifiers.add(beeModifier);

			IModulePage page = state.getPage(FrameHousingPage.class);
			
			for (IHiveFrame frame : getFrames(page.getInventory())) {
				beeModifiers.add(frame.getBeeModifier());
			}

			return beeModifiers;
		}
		return Collections.singleton(beeModifier);
	}
	
	public Collection<IHiveFrame> getFrames(IModuleInventory inventory) {
		Collection<IHiveFrame> hiveFrames = new ArrayList<>(3);

		for (int i = 0; i < 3; i++) {
			ItemStack stackInSlot = inventory.getStackInSlot(i);
			if (stackInSlot == null) {
				continue;
			}

			Item itemInSlot = stackInSlot.getItem();
			if (itemInSlot instanceof IHiveFrame) {
				hiveFrames.add((IHiveFrame) itemInSlot);
			}
		}

		return hiveFrames;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public IModuleState getModuleState() {
		return state;
	}
	
	@Override
	public int getBlockLightValue() {
		return worldObj.getLightFromNeighbors(getCoordinates().up());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeSky(getCoordinates().up());
	}

	@Override
	public GameProfile getOwner() {
		return state.getModular().getHandler().getOwner();
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		BlockPos pos = getCoordinates();
		return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	@Override
	public String getUID() {
		return "BeeHouse";
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		return beeLogic.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		beeLogic.readFromNBT(nbt);
	}

	@Override
	public void addToolTip(List<String> tooltip, ItemStack stack, IModuleState state) {
		
	}

}
