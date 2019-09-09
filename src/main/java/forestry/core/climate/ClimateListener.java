package forestry.core.climate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateProvider;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IWorldClimateHolder;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ILocatable;
import forestry.core.network.packets.PacketClimateListenerUpdate;
import forestry.core.network.packets.PacketClimateListenerUpdateEntity;
import forestry.core.network.packets.PacketClimateListenerUpdateEntityRequest;
import forestry.core.network.packets.PacketClimateListenerUpdateRequest;
import forestry.core.render.ParticleRender;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

public class ClimateListener implements IClimateListener {
	public static final int SERVER_UPDATE = 250;

	private final Object locationProvider;
	@Nullable
	protected World world;
	@Nullable
	protected BlockPos pos;
	private IClimateState cachedState = AbsentClimateState.INSTANCE;
	private IClimateState cachedClientState = AbsentClimateState.INSTANCE;
	@OnlyIn(Dist.CLIENT)
	private TickHelper tickHelper;
	@OnlyIn(Dist.CLIENT)
	protected boolean needsClimateUpdate;
	//The total world time at the moment the cached state has been updated
	private long cacheTime = 0;
	private long lastUpdate = 0;

	public ClimateListener(Object locationProvider) {
		this.locationProvider = locationProvider;
		if (FMLEnvironment.dist == Dist.CLIENT) {
			tickHelper = new TickHelper();
			needsClimateUpdate = true;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void updateClientSide(boolean spawnParticles) {
		if (spawnParticles) {
			tickHelper.onTick();
			if (cachedState.isPresent() && tickHelper.updateOnInterval(20)) {
				World worldObj = getWorldObj();
				BlockPos coordinates = getCoordinates();
				ParticleRender.addTransformParticles(worldObj, coordinates, worldObj.rand);
			}
		}
		if (needsClimateUpdate) {
			if (locationProvider instanceof Entity) {
				NetworkUtil.sendToServer(new PacketClimateListenerUpdateEntityRequest((Entity) locationProvider));
			} else {
				NetworkUtil.sendToServer(new PacketClimateListenerUpdateRequest(getCoordinates()));
			}
			needsClimateUpdate = false;
		}
	}

	private void updateState(boolean syncToClient) {
		IWorldClimateHolder climateHolder = ClimateManager.climateRoot.getWorldClimate(getWorldObj());
		long totalTime = getWorldObj().getGameTime();
		if (cacheTime + SERVER_UPDATE > totalTime && climateHolder.getLastUpdate(getCoordinates()) == lastUpdate) {
			return;
		}
		lastUpdate = climateHolder.getLastUpdate(getCoordinates());
		cachedState = climateHolder.getState(getCoordinates());
		cacheTime = totalTime;
		if (syncToClient) {
			syncToClient();
		}
	}

	private IClimateState getState() {
		return getState(true);
	}

	private IClimateState getState(boolean update) {
		return getState(update, true);
	}

	private IClimateState getState(boolean update, boolean syncToClient) {
		World worldObj = getWorldObj();
		if (!worldObj.isRemote && update) {
			updateState(syncToClient);
		}
		return cachedState;
	}

	private IClimateProvider getDefaultProvider() {
		IClimateProvider provider;
		if (locationProvider instanceof IClimateProvider) {
			provider = (IClimateProvider) locationProvider;
		} else {
			provider = ClimateRoot.getInstance().getDefaultClimate(getWorldObj(), getCoordinates());
		}
		return provider;
	}

	@Override
	public Biome getBiome() {
		IClimateProvider provider = getDefaultProvider();
		return provider.getBiome();
	}

	@Override
	public EnumTemperature getTemperature() {
		Biome biome = getBiome();
		if (BiomeHelper.isBiomeHellish(biome)) {
			return EnumTemperature.HELLISH;
		}
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		IClimateState climateState = getState();
		float temperature;
		if (climateState.isPresent()) {
			temperature = climateState.getTemperature();
		} else {
			Biome biome = getBiome();
			temperature = biome.getTemperature(getCoordinates());
		}
		return temperature;
	}

	@Override
	public float getExactHumidity() {
		IClimateState climateState = getState();
		float humidity;
		if (climateState.isPresent()) {
			humidity = climateState.getHumidity();
		} else {
			Biome biome = getBiome();
			humidity = biome.getDownfall();
		}
		return humidity;
	}

	@Override
	public IClimateState getClimateState() {
		return ClimateStateHelper.of(getExactTemperature(), getExactHumidity());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void setClimateState(IClimateState climateState) {
		this.cachedState = climateState;
	}

	@Override
	public void syncToClient() {
		if (!cachedState.equals(cachedClientState)) {
			World worldObj = getWorldObj();
			if (!worldObj.isRemote) {
				BlockPos coordinates = getCoordinates();
				if (locationProvider instanceof Entity) {
					NetworkUtil.sendNetworkPacket(new PacketClimateListenerUpdateEntity((Entity) locationProvider, cachedState), coordinates, worldObj);
				} else {
					NetworkUtil.sendNetworkPacket(new PacketClimateListenerUpdate(getCoordinates(), cachedState), coordinates, getWorldObj());
				}
			}
			cachedClientState = cachedState;
		}
	}

	@Override
	public void syncToClient(ServerPlayerEntity player) {
		World worldObj = getWorldObj();
		if (!worldObj.isRemote) {
			IClimateState climateState = getState(true, false);
			if (locationProvider instanceof Entity) {
				NetworkUtil.sendToPlayer(new PacketClimateListenerUpdateEntity((Entity) locationProvider, climateState), player);
			} else {
				NetworkUtil.sendToPlayer(new PacketClimateListenerUpdate(getCoordinates(), climateState), player);
			}
		}
	}

	@Override
	public BlockPos getCoordinates() {
		if (this.pos == null) {
			initLocation();
		}
		return this.pos;
	}

	@Override
	public World getWorldObj() {
		if (this.world == null) {
			initLocation();
		}
		return this.world;
	}

	@Override
	public void markLocatableDirty() {
		this.world = null;
		this.pos = null;
		World worldObj = getWorldObj();
		if (!worldObj.isRemote) {
			updateState(true);
		}
	}

	private void initLocation() {
		if ((this.locationProvider instanceof ILocatable)) {
			ILocatable provider = (ILocatable) this.locationProvider;
			this.world = provider.getWorldObj();
			this.pos = provider.getCoordinates();
		} else if ((this.locationProvider instanceof TileEntity)) {
			TileEntity provider = (TileEntity) this.locationProvider;
			this.world = provider.getWorld();
			this.pos = provider.getPos();
		} else {
			throw new IllegalStateException("no / incompatible location provider");
		}
	}
}
