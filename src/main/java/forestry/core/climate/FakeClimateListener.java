package forestry.core.climate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateState;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public class FakeClimateListener implements IClimateListener {

	public static final FakeClimateListener INSTANCE = new FakeClimateListener();

	private FakeClimateListener() {
	}

	@Override
	public IClimateState getClimateState() {
		return AbsentClimateState.INSTANCE;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void updateClientSide(boolean spawnParticles) {

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void setClimateState(IClimateState climateState) {
	}

	@Override
	public Biome getBiome() {
		return ForgeRegistries.BIOMES.getDelegateOrThrow(Biomes.PLAINS).value();
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.NORMAL;
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.NORMAL;
	}

	@Override
	public float getExactTemperature() {
		return 0.0F;
	}

	@Override
	public float getExactHumidity() {
		return 0.0F;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void syncToClient() {

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void syncToClient(ServerPlayer player) {

	}

	@Override
	public BlockPos getCoordinates() {
		return BlockPos.ZERO;
	}

	@Override
	public Level getWorldObj() {
		return null;
	}

	@Override
	public void markLocatableDirty() {
	}
}
