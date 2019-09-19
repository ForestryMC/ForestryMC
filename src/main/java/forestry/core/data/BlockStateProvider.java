package forestry.core.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.state.IProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.blocks.BlockForestryDoor;
import forestry.arboriculture.blocks.BlockForestryFenceGate;
import forestry.arboriculture.blocks.BlockForestryLog;
import forestry.arboriculture.blocks.BlockForestryPlank;
import forestry.arboriculture.blocks.BlockForestrySlab;
import forestry.arboriculture.blocks.BlockForestryStairs;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.modules.features.FeatureBlock;

public class BlockStateProvider implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	protected final Map<Block, Builder> blockToBuilder = Maps.newLinkedHashMap();
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

				cache.func_208316_a(path, s1);
			} catch (IOException ioexception) {
				LOGGER.error("Couldn't save models to {}", path, ioexception);
			}

		});
	}

	public void registerStates() {
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS.getFeatureByType().entrySet()) {
			addPlank(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_FIREPROOF.getFeatureByType().entrySet()) {
			addPlank(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addPlank(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS.getFeatureByType().entrySet()) {
			addLog(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_FIREPROOF.getFeatureByType().entrySet()) {
			addLog(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addLog(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS.getFeatureByType().entrySet()) {
			addStair(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_FIREPROOF.getFeatureByType().entrySet()) {
			addStair(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addStair(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS.getFeatureByType().entrySet()) {
			addSlab(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_FIREPROOF.getFeatureByType().entrySet()) {
			addSlab(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addSlab(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_FIREPROOF.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryDoor, BlockItem>> stair : ArboricultureBlocks.DOORS.getFeatureByType().entrySet()) {
			addDoor(stair.getValue(), stair.getKey());
		}
		//Replaced by the model loader later
		for (BlockDecorativeLeaves leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getBlocks()) {
			addVariants(leaves, new Builder().always(variant -> variant.model = "forestry:block/leaves"));
		}
		for (BlockDefaultLeavesFruit leaves : ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.getBlocks()) {
			addVariants(leaves, new Builder().always(variant -> variant.model = "forestry:block/leaves"));
		}
		for (BlockDefaultLeaves leaves : ArboricultureBlocks.LEAVES_DEFAULT.getBlocks()) {
			addVariants(leaves, new Builder().always(variant -> variant.model = "forestry:block/leaves"));
		}
	}

	private void addPlank(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		addVariants(feature.block(), new Builder().always((variant -> variant.model = "forestry:block/arboriculture/planks/" + type.getName())));
	}

	private void addLog(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		addVariants(feature.block(), new Builder()
			.always((variant) -> variant.model = "forestry:block/arboriculture/logs/" + type.getName())
			.property(BlockStateProperties.AXIS, Direction.Axis.X, (variant) -> variant.x = variant.y = 90)
			.property(BlockStateProperties.AXIS, Direction.Axis.Z, (variant) -> variant.x = 90));
	}

	private void addStair(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = "forestry:block/arboriculture/stairs/" + type.getName();
		BlockState defaultState = feature.defaultState();
		addVariants(feature.block(), new Builder()
			.ignore(StairsBlock.WATERLOGGED)
			.always((variant) -> variant.uvLock = true)
			.property(StairsBlock.HALF, Half.TOP, (variant) -> variant.x = 180)
			.property(StairsBlock.SHAPE, StairsShape.INNER_LEFT, (variant) -> variant.model = modelLocation + "_inner")
			.property(StairsBlock.SHAPE, StairsShape.INNER_RIGHT, (variant) -> variant.model = modelLocation + "_inner")
			.property(StairsBlock.SHAPE, StairsShape.OUTER_LEFT, (variant) -> variant.model = modelLocation + "_outer")
			.property(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT, (variant) -> variant.model = modelLocation + "_outer")
			.property(StairsBlock.SHAPE, StairsShape.STRAIGHT, (variant) -> variant.model = modelLocation)
			.property(StairsBlock.FACING, Direction.WEST, (variant) -> variant.y = 180)
			.property(StairsBlock.FACING, Direction.SOUTH, (variant) -> variant.y = 90)
			.property(StairsBlock.FACING, Direction.NORTH, (variant) -> variant.y = 270)
			.state(defaultState.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.y = 270)
			.state(defaultState.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.y = 90)
			.state(defaultState.with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.y = 0)
			.state(defaultState.with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.y = 180)
			.state(defaultState.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.y = 270)
			.state(defaultState.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.y = 90)
			.state(defaultState.with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.y = 0)
			.state(defaultState.with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, Half.BOTTOM).with(StairsBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.y = 180)
			.state(defaultState.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.y = 90)
			.state(defaultState.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.y = 270)
			.state(defaultState.with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.y = 180)
			.state(defaultState.with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.y = 0)
			.state(defaultState.with(StairsBlock.FACING, Direction.EAST).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.y = 90)
			.state(defaultState.with(StairsBlock.FACING, Direction.WEST).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.y = 270)
			.state(defaultState.with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.y = 180)
			.state(defaultState.with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, Half.TOP).with(StairsBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.y = 0)
		);
	}

	private void addSlab(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = "forestry:block/arboriculture/slabs/" + type.getName();
		addVariants(feature.block(), new Builder()
			.ignore(SlabBlock.WATERLOGGED)
			.property(SlabBlock.TYPE, SlabType.TOP, (variant) -> variant.model = modelLocation + "_top")
			.property(SlabBlock.TYPE, SlabType.BOTTOM, (variant) -> variant.model = modelLocation)
			.property(SlabBlock.TYPE, SlabType.DOUBLE, (variant) -> variant.model = "forestry:block/arboriculture/planks/" + type.getName()));
	}

	private void addFenceGate(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = "forestry:block/arboriculture/fence_gates/" + type.getName();
		addVariants(feature.block(), new Builder()
			.always((variant) -> variant.uvLock = true)
			.property(FenceGateBlock.HORIZONTAL_FACING, Direction.WEST, (variant) -> variant.y = 90)
			.property(FenceGateBlock.HORIZONTAL_FACING, Direction.EAST, (variant) -> variant.y = 270)
			.property(FenceGateBlock.HORIZONTAL_FACING, Direction.NORTH, (variant) -> variant.y = 180)
			.property(FenceGateBlock.IN_WALL, false, FenceGateBlock.OPEN, false, (variant) -> variant.model = modelLocation)
			.property(FenceGateBlock.IN_WALL, false, FenceGateBlock.OPEN, true, (variant) -> variant.model = modelLocation + "_open")
			.property(FenceGateBlock.IN_WALL, true, FenceGateBlock.OPEN, false, (variant) -> variant.model = modelLocation + "_wall")
			.property(FenceGateBlock.IN_WALL, true, FenceGateBlock.OPEN, true, (variant) -> variant.model = modelLocation + "_wall_open"));
	}

	private void addDoor(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		/*String modelLocation = "forestry:block/arboriculture/doors/" + type.getName();
		addVariants(feature.block(), new Builder()
			.property(DoorBlock.FACING, Direction.SOUTH, (variant)-> variant.y = 90)
			.property(DoorBlock.FACING, Direction.WEST, (variant)-> variant.y = 180)
			.property(DoorBlock.FACING, Direction.NORTH, (variant)-> variant.y = 270)
			.property());*/
	}

	protected Path makePath(ResourceLocation location) {
		return this.generator.getOutputFolder().resolve("assets/" + location.getNamespace() + "/blockstates/" + location.getPath() + ".json");
	}

	public void addVariants(Block block, Builder builder) {
		blockToBuilder.put(block, builder);
		builder.defaultState = block.getDefaultState();
	}

	@Override
	public String getName() {
		return "Block State Provider";
	}

	public static class Builder {
		private final Multimap<Integer, Pair<Predicate<BlockState>, Consumer<Variant>>> variants = HashMultimap.create();
		private final List<Property> ignored = new LinkedList<>();
		private BlockState defaultState;

		public <T extends Comparable<T>> Builder property(Property<T> property, T value, Consumer<Variant> consumer) {
			return condition(1, (state) -> state.get(property) == value, consumer);
		}

		public <T extends Comparable<T>, V extends Comparable<V>> Builder property(Property<T> property, T value, Property<V> propertyTwo, V valueTwo, Consumer<Variant> consumer) {
			return condition(1, (state) -> state.get(property) == value && state.get(propertyTwo) == valueTwo, consumer);
		}

		public <T extends Comparable<T>> Builder state(BlockState state, Consumer<Variant> consumer) {
			return condition(0, (s) -> areStateEqual(s, state), consumer);
		}

		@SuppressWarnings("unchecked")
		private boolean areStateEqual(BlockState a, BlockState b) {
			for (Property i : ignored) {
				a = withDefault(a, i);
				b = withDefault(b, i);
			}
			return a.equals(b);
		}

		private <V extends Comparable<V>> BlockState withDefault(BlockState state, Property<V> property) {
			return state.with(property, defaultState.get(property));
		}

		public Builder always(Consumer<Variant> consumer) {
			return condition(2, (state) -> true, consumer);
		}

		public Builder ignore(Property property) {
			ignored.add(property);
			return this;
		}

		public Builder condition(int priority, Predicate<BlockState> predicate, Consumer<Variant> consumer) {
			variants.put(priority, Pair.of(predicate, consumer));
			return this;
		}

		public JsonObject serialize(Block block) {
			JsonObject obj = new JsonObject();
			JsonObject variants = new JsonObject();
			for (BlockState state : block.getStateContainer().getValidStates()) {
				Variant variant = new Variant();
				for (int i = 2; i >= 0; i--) {
					for (Pair<Predicate<BlockState>, Consumer<Variant>> entry : this.variants.get(i)) {
						if (entry.getFirst().test(state)) {
							entry.getSecond().accept(variant);
						}
					}
				}
				Map<IProperty<?>, Comparable<?>> properties = new HashMap<>(state.getValues());
				ignored.forEach(properties::remove);
				variants.add(BlockModelShapes.getPropertyMapString(properties), variant.serialize());
			}
			obj.add("variants", variants);
			return obj;
		}
	}

	private static class Variant {
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

	public static class MultipartBuilder {
		public final List<Selector> selectors = new LinkedList<>();
	}

	public static class Selector {
		private final ICondition condition;
		private final List<Variant> variantList;

		public Selector(ICondition condition, List<Variant> variantList) {
			this.condition = condition;
			this.variantList = variantList;
		}
	}

	public interface ICondition {

	}
}
