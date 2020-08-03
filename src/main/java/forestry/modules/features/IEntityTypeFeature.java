package forestry.modules.features;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public interface IEntityTypeFeature<E extends Entity> extends IModFeature {

    @Override
    default void create() {
        EntityType<E> entityType = getEntityTypeConstructor().build(getIdentifier());
        entityType.setRegistryName(new ResourceLocation(getModId(), getIdentifier()));
        setEntityType(entityType);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <R extends IForgeRegistryEntry<R>> void register(RegistryEvent.Register<R> event) {
        IForgeRegistry<R> registry = event.getRegistry();
        Class<R> superType = registry.getRegistrySuperType();
        if (EntityType.class.isAssignableFrom(superType) && hasEntityType()) {
            registry.register((R) entityType());
            GlobalEntityTypeAttributes.put((EntityType<? extends LivingEntity>) entityType(), createAttributes().create());
        }
    }

    default EntityType<E> entityType() {
        EntityType<E> tileType = getEntityType();
        if (tileType == null) {
            throw new IllegalStateException("Called feature getter method before content creation.");
        }
        return tileType;
    }

    AttributeModifierMap.MutableAttribute createAttributes();

    void setEntityType(EntityType<E> entityType);

    EntityType.Builder<E> getEntityTypeConstructor();

    boolean hasEntityType();

    @Nullable
    EntityType<E> getEntityType();
}
