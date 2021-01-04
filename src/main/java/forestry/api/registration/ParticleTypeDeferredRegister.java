package forestry.api.registration;


import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ParticleTypeDeferredRegister extends WrappedDeferredRegister<ParticleType<?>> {

    public ParticleTypeDeferredRegister(String modid) {
        super(modid, ForgeRegistries.PARTICLE_TYPES);
    }

    public ParticleTypeRegistryObject<BasicParticleType> registerBasicParticle(String name) {
        return register(name, () -> new BasicParticleType(false));
    }

    public <PARTICLE extends IParticleData> ParticleTypeRegistryObject<PARTICLE> register(
            String name,
            Supplier<ParticleType<PARTICLE>> sup
    ) {
        return register(name, sup, ParticleTypeRegistryObject::new);
    }
}