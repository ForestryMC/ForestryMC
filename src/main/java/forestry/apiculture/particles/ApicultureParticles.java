package forestry.apiculture.particles;

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
public class ApicultureParticles {
    public static final ParticleTypeDeferredRegister PARTICLE_TYPES = new ParticleTypeDeferredRegister(Constants.MOD_ID);

    public static final ParticleTypeRegistryObject<BeeParticleData> BEE_EXPLORER_PARTICLE = PARTICLE_TYPES.register(
            "bee_explore_particle",
            BeeParticleType::new
    );

    public static final ParticleTypeRegistryObject<BeeParticleData> BEE_ROUND_TRIP_PARTICLE = PARTICLE_TYPES.register(
            "bee_round_trip_particle",
            BeeParticleType::new
    );

    public static final ParticleTypeRegistryObject<BeeParticleData> BEE_TARGET_ENTITY_PARTICLE = PARTICLE_TYPES.register(
            "bee_target_entity_particle",
            BeeParticleType::new
    );

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void registerParticleFactory(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(
                ApicultureParticles.BEE_EXPLORER_PARTICLE.getParticleType(),
                BeeExploreParticle.Factory::new
        );

        Minecraft.getInstance().particles.registerFactory(
                ApicultureParticles.BEE_ROUND_TRIP_PARTICLE.getParticleType(),
                BeeRoundTripParticle.Factory::new
        );

        Minecraft.getInstance().particles.registerFactory(
                ApicultureParticles.BEE_TARGET_ENTITY_PARTICLE.getParticleType(),
                BeeTargetEntityParticle.Factory::new
        );
    }
}
