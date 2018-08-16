package forestry.core.entities;

import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.world.World;

import forestry.api.core.EnumTemperature;
import forestry.core.utils.ColourUtil;

public class ParticleClimate extends ParticleRedstone {
	public ParticleClimate(World world, double x, double y, double z, EnumTemperature temperature) {
		this(world, x, y, z, temperature.color);
	}

	public ParticleClimate(World world, double x, double y, double z) {
		this(world, x, y, z, 0x37485a);
	}

	private ParticleClimate(World worldIn, double x, double y, double z, int color) {
		super(worldIn, x, y, z, 0.0F, 0.0F, 0.0F);
		particleRed = ColourUtil.getRedAsFloat(color);
		particleGreen = ColourUtil.getGreenAsFloat(color);
		particleBlue = ColourUtil.getBlueAsFloat(color);
	}
}
