package forestry.modules.features;

import com.google.common.collect.ImmutableTable;
import forestry.api.core.IFeatureSubtype;
import forestry.api.core.IItemProvider;
import forestry.modules.features.FeatureGroup.IdentifierType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class FeatureTable<B extends FeatureTable.Builder<R, C, ? extends FeatureTable<B, F, R, C>>, F extends IModFeature, R extends IFeatureSubtype, C extends IFeatureSubtype> {

    protected final ImmutableTable<R, C, F> featureByTypes;

    public FeatureTable(B builder) {
        ImmutableTable.Builder<R, C, F> mapBuilder = new ImmutableTable.Builder<>();
        for (R row : builder.rowTypes) {
            for (C column : builder.columnTypes) {
                mapBuilder.put(row, column, createFeature(builder, row, column));
            }
        }
        featureByTypes = mapBuilder.build();
    }

    protected abstract F createFeature(B builder, R rowType, C columnType);

    public boolean has(R rowType, C columnType) {
        return featureByTypes.contains(rowType, columnType);
    }

    public F get(R rowType, C columnType) {
        return featureByTypes.get(rowType, columnType);
    }

    public ImmutableTable<R, C, F> getFeatureByTypes() {
        return featureByTypes;
    }

    public Collection<F> getRowFeatures(R rowType) {
        return featureByTypes.row(rowType).values();
    }

    public Collection<F> getColumnFeatures(C rowType) {
        return featureByTypes.column(rowType).values();
    }

    public Collection<F> getFeatures() {
        return featureByTypes.values();
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

    public ItemStack stack(R rowType, C columnType) {
        return stack(rowType, columnType, 1);
    }

    public ItemStack stack(R rowType, C columnType, int amount) {
        F featureBlock = featureByTypes.get(rowType, columnType);
        if (!(featureBlock instanceof IItemProvider)) {
            throw new IllegalStateException(
                    "This feature group has no item registered for the given sub type to create a stack for.");
        }
        return ((IItemProvider) featureBlock).stack(amount);
    }

    public ItemStack stack(R rowType, C columnType, StackOption... options) {
        F featureBlock = featureByTypes.get(rowType, columnType);
        if (!(featureBlock instanceof IItemProvider)) {
            throw new IllegalStateException(
                    "This feature group has no item registered for the given sub type to create a stack for.");
        }
        return ((IItemProvider) featureBlock).stack(options);
    }

    public static abstract class Builder<R extends IFeatureSubtype, C extends IFeatureSubtype, G> {
        protected final IFeatureRegistry registry;
        protected final Set<R> rowTypes = new HashSet<>();
        protected final Set<C> columnTypes = new HashSet<>();
        protected IdentifierType identifierType = IdentifierType.TYPE_ONLY;
        protected String identifier = StringUtils.EMPTY;

        public Builder(IFeatureRegistry registry) {
            this.registry = registry;
        }

        public Builder<R, C, G> identifier(String identifier) {
            return identifier(identifier, IdentifierType.PREFIX);
        }

        public Builder<R, C, G> identifier(String identifier, IdentifierType type) {
            this.identifier = identifier;
            this.identifierType = type;
            return this;
        }

        public Builder<R, C, G> rowType(R type) {
            rowTypes.add(type);
            return this;
        }

        public Builder<R, C, G> rowTypes(R[] types) {
            return rowTypes(Arrays.asList(types));
        }

        public Builder<R, C, G> rowTypes(Collection<R> types) {
            rowTypes.addAll(types);
            return this;
        }

        public Builder<R, C, G> columnType(C type) {
            columnTypes.add(type);
            return this;
        }

        public Builder<R, C, G> columnTypes(C[] types) {
            return columnTypes(Arrays.asList(types));
        }

        public Builder<R, C, G> columnTypes(Collection<C> types) {
            columnTypes.addAll(types);
            return this;
        }

        protected String getIdentifier(IFeatureSubtype rowType, IFeatureSubtype columnType) {
            return identifierType.apply(identifier, rowType.getString() + "_" + columnType.getString());
        }

        public abstract G create();
    }
}
