package forestry.apiculture.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;

import com.mojang.serialization.Codec;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import forestry.core.config.Constants;
import forestry.core.registration.ParticleTypeDeferredRegister;
import forestry.core.registration.ParticleTypeRegistryObject;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ApicultureParticles {
	public static final ParticleTypeDeferredRegister PARTICLE_TYPES = new ParticleTypeDeferredRegister(Constants.MOD_ID);

	public static final ParticleTypeRegistryObject<BeeParticleData> BEE_EXPLORER_PARTICLE = PARTICLE_TYPES.register("bee_explore_particle", BeeParticleType::new);

	public static final ParticleTypeRegistryObject<BeeParticleData> BEE_ROUND_TRIP_PARTICLE = PARTICLE_TYPES.register("bee_round_trip_particle", BeeParticleType::new);

	public static final ParticleTypeRegistryObject<BeeTargetParticleData> BEE_TARGET_ENTITY_PARTICLE = PARTICLE_TYPES.register("bee_target_entity_particle", () -> new ParticleType<BeeTargetParticleData>(false, BeeTargetParticleData.DESERIALIZER) {

		@Override
		public Codec<BeeTargetParticleData> codec() {
			return BeeTargetParticleData.CODEC;
		}
	});

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void registerParticleFactory(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(ApicultureParticles.BEE_EXPLORER_PARTICLE.getParticleType(), BeeExploreParticle.Factory::new);

		Minecraft.getInstance().particleEngine.register(ApicultureParticles.BEE_ROUND_TRIP_PARTICLE.getParticleType(), BeeRoundTripParticle.Factory::new);

		Minecraft.getInstance().particleEngine.register(ApicultureParticles.BEE_TARGET_ENTITY_PARTICLE.getParticleType(), BeeTargetEntityParticle.Factory::new);
	}
}
