package forestry.modules.features;

import com.google.common.collect.ImmutableMap;
import forestry.api.core.IFeatureSubtype;
import forestry.api.core.IItemProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiFunction;

public abstract class FeatureGroup<B extends FeatureGroup.Builder<S, ? extends FeatureGroup<B, F, S>>, F extends IModFeature, S extends IFeatureSubtype> {

    protected final ImmutableMap<S, F> featureByType;

    protected FeatureGroup(B builder) {
        ImmutableMap.Builder<S, F> mapBuilder = new ImmutableMap.Builder<>();
        builder.subTypes.forEach(subType -> mapBuilder.put(subType, createFeature(builder, subType)));
        featureByType = mapBuilder.build();
    }

    protected abstract F createFeature(B builder, S type);

    public boolean has(S subType) {
        return featureByType.containsKey(subType);
    }

    public F get(S subType) {
        return featureByType.get(subType);
    }

    public ImmutableMap<S, F> getFeatureByType() {
        return featureByType;
    }

    public Collection<F> getFeatures() {
        return featureByType.values();
    }

    public Optional<F> findFeature(String typeName) {
        return featureByType.entrySet().stream()
                            .filter(e -> e.getKey().getString().equals(typeName))
                            .findFirst()
                            .flatMap(e -> Optional.of(e.getValue()));
    }

    public boolean itemEqual(ItemStack stack) {
        return getFeatures().stream()
                            .filter(f -> f instanceof IItemProvider)
                            .map(f -> (IItemProvider) f)
                            .anyMatch(f -> f.itemEqual(stack));
    }

    public boolean itemEqual(Item item) {
        return getFeatures().stream()
                            .filter(f -> f instanceof IItemProvider)
                            .map(f -> (IItemProvider) f)
                            .anyMatch(f -> f.itemEqual(item));
    }

    public ItemStack stack(S subType) {
        return stack(subType, 1);
    }

    public ItemStack stack(S subType, int amount) {
        F featureBlock = featureByType.get(subType);
        if (!(featureBlock instanceof IItemProvider)) {
            throw new IllegalStateException(
                    "This feature group has no item registered for the given sub type to create a stack for.");
        }
        return ((IItemProvider) featureBlock).stack(amount);
    }

    public ItemStack stack(S subType, StackOption... options) {
        F featureBlock = featureByType.get(subType);
        if (!(featureBlock instanceof IItemProvider)) {
            throw new IllegalStateException(
                    "This feature group has no item registered for the given sub type to create a stack for.");
        }
        return ((IItemProvider) featureBlock).stack(options);
    }

    public static abstract class Builder<S extends IFeatureSubtype, G> {
        protected final IFeatureRegistry registry;
        protected final Set<S> subTypes = new HashSet<>();
        protected IdentifierType identifierType = IdentifierType.TYPE_ONLY;
        protected String identifier = StringUtils.EMPTY;

        public Builder(IFeatureRegistry registry) {
            this.registry = registry;
        }

        public Builder<S, G> identifier(String identifier) {
            return identifier(identifier, IdentifierType.PREFIX);
        }

        public Builder<S, G> identifier(String identifier, IdentifierType type) {
            this.identifier = identifier;
            this.identifierType = type;
            return this;
        }

        public Builder<S, G> type(S type) {
            subTypes.add(type);
            return this;
        }

        public Builder<S, G> types(S[] types) {
            return types(Arrays.asList(types));
        }

        public Builder<S, G> types(Collection<S> types) {
            subTypes.addAll(types);
            return this;
        }

        protected String getIdentifier(IFeatureSubtype type) {
            return identifierType.apply(identifier, type.getString());
        }

        public abstract G create();
    }

    public enum IdentifierType implements BiFunction<String, String, String> {
        TYPE_ONLY {
            @Override
            public String apply(String feature, String type) {
                return type;
            }
        },
        PREFIX {
            @Override
            public String apply(String feature, String type) {
                return feature + '_' + type;
            }
        },
        AFFIX {
            @Override
            public String apply(String feature, String type) {
                return type + '_' + feature;
            }
        }
    }
}
