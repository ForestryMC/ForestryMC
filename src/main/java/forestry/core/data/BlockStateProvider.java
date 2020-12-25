package forestry.core.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class BlockStateProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    protected final Map<Block, IBuilder> blockToBuilder = Maps.newLinkedHashMap();
    protected final DataGenerator generator;

    public BlockStateProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        this.blockToBuilder.clear();
        this.registerStates();
        blockToBuilder.forEach((key, builder) -> {
            if (key.getRegistryName() == null) {
                return;
            }
            JsonObject jsonobject = builder.serialize(key);
            Path path = this.makePath(key.getRegistryName());
            try {
                String s = GSON.toJson(jsonobject);
                String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
                if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                        bufferedwriter.write(s);
                    }
                }

                cache.recordHash(path, s1);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save models to {}", path, ioexception);
            }

        });
    }

    public abstract void registerStates();

    protected Path makePath(ResourceLocation location) {
        return this.generator.getOutputFolder().resolve(
                "assets/" + location.getNamespace() + "/blockstates/" + location.getPath() + ".json");
    }

    public void addVariants(Block block, IBuilder builder) {
        blockToBuilder.put(block, builder);
    }

    @Override
    public String getName() {
        return "Block State Provider";
    }

    public interface ICondition {
        JsonElement serialize();

        String getName();
    }

    public interface IBuilder {
        JsonObject serialize(Block block);
    }

    public static class Builder implements IBuilder {
        private final List<Consumer<Variant>> always = new LinkedList<>();
        private final Map<Predicate<BlockState>, Consumer<Variant>> variants = new HashMap<>();
        private final Multimap<BlockState, Consumer<Variant>> blockStateVariants = HashMultimap.create();
        private final Deque<List<Property>> ignored = new ArrayDeque<>();
        private final List<Property> alwaysIgnore = new LinkedList<>();

        public Builder push() {
            ignored.addFirst(new LinkedList<>());
            return this;
        }

        public Builder popIgnore() {
            ignored.pop();
            return this;
        }

        public Builder alwaysIgnore(Property... properties) {
            alwaysIgnore.addAll(Arrays.asList(properties));
            return this;
        }

        public <T extends Comparable<T>> Builder property(Property<T> property, T value, Consumer<Variant> consumer) {
            return condition((state) -> state.get(property) == value, consumer);
        }

        public <T extends Comparable<T>, V extends Comparable<V>> Builder property(
                Property<T> property,
                T value,
                Property<V> propertyTwo,
                V valueTwo,
                Consumer<Variant> consumer
        ) {
            return condition((state) -> state.get(property) == value && state.get(propertyTwo) == valueTwo, consumer);
        }

        public <T extends Comparable<T>> Builder state(BlockState state, Consumer<Variant> consumer) {
            Set<BlockState> mappedStates = new HashSet<>();
            mappedStates.add(state);
            List<Property> ignoredProperties = new LinkedList<>();
            ignored.forEach(ignoredProperties::addAll);
            ignoredProperties.addAll(alwaysIgnore);
            for (Property property : ignoredProperties) {
                //noinspection unchecked
                mappedStates = mapStates(property, mappedStates);
            }
            mappedStates.forEach(mappedState -> blockStateVariants.put(mappedState, consumer));
            return this;
        }

        private <V extends Comparable<V>> Set<BlockState> mapStates(Property<V> property, Set<BlockState> states) {
            Set<BlockState> mappedStates = new HashSet<>();
            for (V value : property.getAllowedValues()) {
                states.forEach(mappedState -> mappedStates.add(mappedState.with(property, value)));
            }
            return mappedStates;
        }

        public Builder always(Consumer<Variant> consumer) {
            always.add(consumer);
            return this;
        }

        public Builder ignore(Property property) {
            List<Property> properties;
            if (ignored.isEmpty()) {
                ignored.addFirst(properties = new LinkedList<>());
            } else {
                properties = ignored.getFirst();
            }
            properties.add(property);
            return this;
        }

        public Builder condition(Predicate<BlockState> predicate, Consumer<Variant> consumer) {
            variants.put(predicate, consumer);
            return this;
        }

        public JsonObject serialize(Block block) {
            JsonObject obj = new JsonObject();
            JsonObject variantsObj = new JsonObject();
            Variant defaultVariant = new Variant();
            always.forEach(consumer -> consumer.accept(defaultVariant));
            for (BlockState state : block.getStateContainer().getValidStates()) {
                Variant variant = defaultVariant.copy();
                for (Map.Entry<Predicate<BlockState>, Consumer<Variant>> entry : this.variants.entrySet()) {
                    if (entry.getKey().test(state)) {
                        entry.getValue().accept(variant);
                    }
                }
                blockStateVariants.get(state).forEach(consumer -> consumer.accept(variant));
                Map<Property<?>, Comparable<?>> properties = new HashMap<>(state.getValues());
                alwaysIgnore.forEach(properties::remove);
                variantsObj.add(BlockModelShapes.getPropertyMapString(properties), variant.serialize());
            }
            obj.add("variants", variantsObj);
            return obj;
        }
    }

    public static class Variant {
        private String model = "block/block";
        @Nullable
        private Boolean uvLock = null;
        private int weight = -1;
        private int x = -1;
        private int y = -1;

        private JsonObject serialize() {
            JsonObject obj = new JsonObject();
            obj.addProperty("model", model);
            if (uvLock != null) {
                obj.addProperty("uvlock", uvLock);
            }
            if (weight >= 0) {
                obj.addProperty("weight", weight);
            }
            if (x > 0) {
                obj.addProperty("x", x);
            }
            if (y > 0) {
                obj.addProperty("y", y);
            }
            return obj;
        }

        private Variant copy() {
            Variant variant = new Variant();
            variant.uvLock = uvLock;
            variant.model = model;
            variant.weight = weight;
            variant.x = x;
            variant.y = y;
            return variant;
        }

        public Variant model(String model) {
            this.model = model;
            return this;
        }

        public Variant lock(@Nullable Boolean uvLock) {
            this.uvLock = uvLock;
            return this;
        }

        public Variant weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Variant rotationX(int x) {
            this.x = x;
            return this;
        }

        public Variant rotationY(int y) {
            this.y = y;
            return this;
        }
    }

    public static class MultipartBuilder implements IBuilder {
        public final List<Selector> selectors = new LinkedList<>();

        public <V extends Comparable<V>> MultipartBuilder always(UnaryOperator<Variant> builder) {
            selectors.add(new Selector(null, Collections.singletonList(builder.apply(new Variant()))));
            return this;
        }

        public <V extends Comparable<V>> MultipartBuilder property(
                UnaryOperator<Variant> builder,
                Property<V> property,
                V... values
        ) {
            return and(builder, new ConditionBuilder().property(property, values));
        }

        public MultipartBuilder or(UnaryOperator<Variant> builder, ConditionBuilder condition) {
            selectors.add(new Selector(
                    new OrCondition(condition.conditions),
                    Collections.singletonList(builder.apply(new Variant()))
            ));
            return this;
        }

        public MultipartBuilder and(UnaryOperator<Variant> builder, ConditionBuilder condition) {
            selectors.add(new Selector(
                    new AndCondition(condition.conditions, false),
                    Collections.singletonList(builder.apply(new Variant()))
            ));
            return this;
        }

        public JsonObject serialize(Block block) {
            JsonObject obj = new JsonObject();
            JsonArray selectorsArray = new JsonArray();
            selectors.forEach(selector -> selectorsArray.add(selector.serialize()));
            obj.add("multipart", selectorsArray);
            return obj;
        }

    }

    public static class ConditionBuilder {
        private final List<ICondition> conditions = new LinkedList<>();

        public <V extends Comparable<V>> ConditionBuilder property(Property<V> property, V... values) {
            conditions.add(new PropertyValueCondition<>(property, false, values));
            return this;
        }

        public <V extends Comparable<V>> ConditionBuilder propertyNegated(Property<V> property, V... values) {
            conditions.add(new PropertyValueCondition<>(property, true, values));
            return this;
        }

        public ConditionBuilder or(ConditionBuilder condition) {
            conditions.add(new OrCondition(condition.conditions));
            return this;
        }

        public ConditionBuilder and(ConditionBuilder condition) {
            conditions.add(new AndCondition(condition.conditions, true));
            return this;
        }
    }

    public static class Selector {
        @Nullable
        private final ICondition condition;
        private final List<Variant> variantList;

        public Selector(@Nullable ICondition condition, List<Variant> variantList) {
            this.condition = condition;
            this.variantList = variantList;
        }

        public JsonElement serialize() {
            JsonObject obj = new JsonObject();
            if (condition != null) {
                obj.add("when", condition.serialize());
            }
            if (variantList.size() > 1) {
                JsonArray variantArray = new JsonArray();
                variantList.forEach(variant -> variantArray.add(variant.serialize()));
                obj.add("apply", variantArray);
            } else {
                obj.add("apply", variantList.get(0).serialize());
            }
            return obj;
        }
    }

    public static class AndCondition implements ICondition {
        private final Iterable<ICondition> conditions;
        private final boolean nested;

        public AndCondition(Iterable<ICondition> conditions, boolean nested) {
            this.conditions = conditions;
            this.nested = nested;
        }

        @Override
        public JsonElement serialize() {
            JsonObject obj = new JsonObject();
            if (nested) {
                JsonArray conditionArray = new JsonArray();
                conditions.forEach(condition -> conditionArray.add(condition.serialize()));
                obj.add("AND", conditionArray);
            } else {
                conditions.forEach(condition -> obj.add(condition.getName(), condition.serialize()));
            }
            return obj;
        }

        @Override
        public String getName() {
            return "AND";
        }
    }

    public static class OrCondition implements ICondition {
        private final Iterable<ICondition> conditions;

        public OrCondition(Iterable<ICondition> conditions) {
            this.conditions = conditions;
        }

        @Override
        public JsonElement serialize() {
            JsonObject obj = new JsonObject();
            JsonArray conditionArray = new JsonArray();
            conditions.forEach(condition -> conditionArray.add(condition.serialize()));
            obj.add("OR", conditionArray);
            return obj;
        }

        @Override
        public String getName() {
            return "OR";
        }
    }

    public static class PropertyValueCondition<V extends Comparable<V>> implements ICondition {
        private final Property<V> property;
        private final V[] values;
        private final boolean negated;

        public PropertyValueCondition(Property<V> property, boolean negated, V... values) {
            this.property = property;
            this.values = values;
            this.negated = negated;
        }

        @Override
        public JsonElement serialize() {
            StringBuilder valueName = new StringBuilder();
            if (negated) {
                valueName.append('!');
            }
            for (int i = 0; i < values.length; i++) {
                V value = values[i];
                if (i > 0) {
                    valueName.append('|');
                }
                valueName.append(property.getName(value));
            }
            return new JsonPrimitive(valueName.toString());
        }

        @Override
        public String getName() {
            return property.getName();
        }
    }
}
