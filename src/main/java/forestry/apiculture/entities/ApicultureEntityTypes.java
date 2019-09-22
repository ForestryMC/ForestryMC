package forestry.apiculture.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

import net.minecraftforge.registries.IForgeRegistry;

//TODO migrate this and tile entities/container types/etc to feature registry
public class ApicultureEntityTypes {

	public final EntityType<MinecartEntityApiary> APIARY_MINECART;
	public final EntityType<MinecartEntityBeehouse> BEE_HOUSE_MINECART;

	private IForgeRegistry<EntityType<?>> registry;


	public ApicultureEntityTypes(IForgeRegistry<EntityType<?>> registry) {
		this.registry = registry;
		APIARY_MINECART = register(EntityType.Builder.<MinecartEntityApiary>create(MinecartEntityApiary::new, EntityClassification.MISC).size(0.98F, 0.7F), "cart_apiary");
		BEE_HOUSE_MINECART = register(EntityType.Builder.<MinecartEntityBeehouse>create(MinecartEntityBeehouse::new, EntityClassification.MISC).size(0.98F, 0.7F), "cart_bee_house");
	}

	private <T extends Entity> EntityType<T> register(EntityType.Builder<T> builder, String name) {
		EntityType<T> type = builder.build(name);
		type.setRegistryName(name);
		registry.register(type);
		return type;
	}
}
