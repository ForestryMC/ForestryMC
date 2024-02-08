package forestry.core.climate;

import javax.annotation.Nullable;

import deleteme.BiomeCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateProvider;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IWorldClimateHolder;
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
	protected Level world;
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
				Level worldObj = getWorldObj();
				BlockPos coordinates = getCoordinates();
				ParticleRender.addTransformParticles(worldObj, coordinates, worldObj.random);
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
		Level worldObj = getWorldObj();
		if (!worldObj.isClientSide && update) {
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
		if (BiomeCategory.NETHER.is(getBiome())) {
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
		if (climateState.isPresent()) {
			return climateState.getTemperature();
		} else {
			return getBiome().getBaseTemperature();
		}
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
			Level worldObj = getWorldObj();
			if (!worldObj.isClientSide) {
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
	public void syncToClient(ServerPlayer player) {
		Level worldObj = getWorldObj();
		if (!worldObj.isClientSide) {
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
	public Level getWorldObj() {
		if (this.world == null) {
			initLocation();
		}
		return this.world;
	}

	@Override
	public void markLocatableDirty() {
		this.world = null;
		this.pos = null;
		Level worldObj = getWorldObj();
		if (!worldObj.isClientSide) {
			updateState(true);
		}
	}

	private void initLocation() {
		if ((this.locationProvider instanceof ILocatable provider)) {
			this.world = provider.getWorldObj();
			this.pos = provider.getCoordinates();
		} else if ((this.locationProvider instanceof BlockEntity provider)) {
			this.world = provider.getLevel();
			this.pos = provider.getBlockPos();
		} else {
			throw new IllegalStateException("no / incompatible location provider");
		}
	}
}
