package forestry.apiculture.particles;

import net.minecraft.core.particles.ParticleType;

import com.mojang.serialization.Codec;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import forestry.core.config.Constants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ApicultureParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Constants.MOD_ID);

	public static final RegistryObject<ParticleType<BeeParticleData>> BEE_EXPLORER_PARTICLE = PARTICLE_TYPES.register("bee_explore_particle", BeeParticleType::new);
	public static final RegistryObject<ParticleType<BeeParticleData>> BEE_ROUND_TRIP_PARTICLE = PARTICLE_TYPES.register("bee_round_trip_particle", BeeParticleType::new);
	public static final RegistryObject<ParticleType<BeeTargetParticleData>> BEE_TARGET_ENTITY_PARTICLE = PARTICLE_TYPES.register("bee_target_entity_particle", () -> new ParticleType<>(false, BeeTargetParticleData.DESERIALIZER) {
		@Override
		public Codec<BeeTargetParticleData> codec() {
			return BeeTargetParticleData.CODEC;
		}
	});

	@SubscribeEvent
	public static void registerParticleFactory(RegisterParticleProvidersEvent event) {
		event.register(ApicultureParticles.BEE_EXPLORER_PARTICLE.get(), BeeExploreParticle.Factory::new);
		event.register(ApicultureParticles.BEE_ROUND_TRIP_PARTICLE.get(), BeeRoundTripParticle.Factory::new);
		event.register(ApicultureParticles.BEE_TARGET_ENTITY_PARTICLE.get(), BeeTargetEntityParticle.Factory::new);
	}
}
