package forestry.api.registration;

import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;

public class ParticleTypeRegistryObject<PARTICLE extends IParticleData> extends WrappedRegistryObject<ParticleType<PARTICLE>> {

    public ParticleTypeRegistryObject(RegistryObject<ParticleType<PARTICLE>> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public ParticleType<PARTICLE> getParticleType() {
        return get();
    }
}
