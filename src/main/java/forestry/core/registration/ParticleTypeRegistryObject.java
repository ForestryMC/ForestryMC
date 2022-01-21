package forestry.core.registration;

import javax.annotation.Nonnull;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import net.minecraftforge.fml.RegistryObject;

public class ParticleTypeRegistryObject<PARTICLE extends ParticleOptions> extends WrappedRegistryObject<ParticleType<PARTICLE>> {

	public ParticleTypeRegistryObject(RegistryObject<ParticleType<PARTICLE>> registryObject) {
		super(registryObject);
	}

	@Nonnull
	public ParticleType<PARTICLE> getParticleType() {
		return get();
	}
}
