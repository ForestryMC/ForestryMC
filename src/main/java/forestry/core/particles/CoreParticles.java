package forestry.core.particles;

import forestry.api.registration.ParticleTypeDeferredRegister;
import forestry.api.registration.ParticleTypeRegistryObject;
import forestry.core.config.Constants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class CoreParticles {
    public static final ParticleTypeDeferredRegister PARTICLE_TYPES = new ParticleTypeDeferredRegister(Constants.MOD_ID);

    public static final ParticleTypeRegistryObject<SnowParticleData> SNOW_PARTICLE = PARTICLE_TYPES.register(
            "snow_particle",
            SnowParticleType::new
    );

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void registerParticleFactory(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(
                CoreParticles.SNOW_PARTICLE.getParticleType(),
                SnowParticle.Factory::new
        );
    }
}
