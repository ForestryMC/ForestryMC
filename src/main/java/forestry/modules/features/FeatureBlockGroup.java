package forestry.modules.features;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.core.IBlockSubtype;

public class FeatureBlockGroup<B extends Block, S extends IBlockSubtype> {

	private final ImmutableMap<S, FeatureBlock<B, BlockItem>> blockByType;

	private FeatureBlockGroup(Builder<B, S> builder) {
		Preconditions.checkNotNull(builder.constructor);
		ImmutableMap.Builder<S, FeatureBlock<B, BlockItem>> mapBuilder = new ImmutableMap.Builder<>();
		builder.subTypes.forEach(subType -> mapBuilder.put(subType, builder.registry.block(() -> builder.constructor.apply(subType), builder.itemConstructor != null ? (block) -> builder.itemConstructor.apply(block, subType) : null, builder.identifierType.apply(builder.identifier, subType.getName()))));
		blockByType = mapBuilder.build();
	}

	public boolean has(S subType) {
		return blockByType.containsKey(subType);
	}

	public FeatureBlock<B, BlockItem> get(S subType) {
		return blockByType.get(subType);
	}

	public Map<S, FeatureBlock<B, BlockItem>> getBlockByType() {
		return Collections.unmodifiableMap(blockByType);
	}

	public Collection<FeatureBlock<B, BlockItem>> getFeatures() {
		return blockByType.values();
	}

	public boolean itemEqual(ItemStack stack) {
		return getFeatures().stream().anyMatch(f -> f.itemEqual(stack));
	}

	public boolean itemEqual(Item item) {
		return getFeatures().stream().anyMatch(f -> f.itemEqual(item));
	}

	public ItemStack stack(S subType) {
		return stack(subType, 1);
	}

	public ItemStack stack(S subType, int amount) {
		FeatureBlock<B, BlockItem> featureBlock = blockByType.get(subType);
		if (featureBlock == null) {
			throw new IllegalStateException("This feature group has no item registered for the given sub type to create a stack for.");
		}
		return featureBlock.stack(amount);
	}

	public ItemStack stack(S subType, StackOption... options) {
		FeatureBlock<B, BlockItem> featureBlock = blockByType.get(subType);
		if (featureBlock == null) {
			throw new IllegalStateException("This feature group has no item registered for the given sub type to create a stack for.");
		}
		return featureBlock.stack(options);
	}

	public Collection<B> getBlocks() {
		return blockByType.values().stream().map(IBlockFeature::block).collect(Collectors.toList());
	}

	public Optional<FeatureBlock<B, BlockItem>> findFeature(String typeName) {
		return blockByType.entrySet().stream()
			.filter(e -> e.getKey().getName().equals(typeName))
			.findFirst()
			.flatMap(e -> Optional.of(e.getValue()));
	}

	@Nullable
	public BlockState findState(String typeName) {
		Optional<FeatureBlock> block = blockByType.entrySet().stream()
			.filter(e -> e.getKey().getName().equals(typeName))
			.findFirst()
			.flatMap(e -> Optional.of(e.getValue()));
		return block.map(FeatureBlock::defaultState).orElse(null);
	}

	public boolean blockEqual(BlockState state) {
		return getFeatures().stream().anyMatch(f -> f.blockEqual(state));
	}

	public boolean blockEqual(Block block) {
		return getFeatures().stream().anyMatch(f -> f.blockEqual(block));
	}

	public Block[] blockArray() {
		return getBlocks().toArray(new Block[0]);
	}

	public static class Builder<B extends Block, S extends IBlockSubtype> {
		private final IFeatureRegistry registry;
		private final Set<S> subTypes = new HashSet<>();
		private IdentifierType identifierType = IdentifierType.TYPE_ONLY;
		private Function<S, B> constructor;
		private String identifier = StringUtils.EMPTY;
		@Nullable
		private BiFunction<B, S, BlockItem> itemConstructor;

		public Builder(IFeatureRegistry registry, Function<S, B> constructor) {
			this.registry = registry;
			this.constructor = constructor;
		}

		@Deprecated
		public Builder<B, S> setIdentType(IdentifierType identifierType) {
			this.identifierType = identifierType;
			return this;
		}

		public Builder<B, S> affix() {
			this.identifierType = IdentifierType.AFFIX;
			return this;
		}

		public Builder<B, S> setIdent(String identifier) {
			return setIdent(identifier, IdentifierType.PREFIX);
		}

		public Builder<B, S> setIdent(String identifier, IdentifierType type) {
			this.identifier = identifier;
			this.identifierType = type;
			return this;
		}

		public Builder<B, S> setItem(BiFunction<B, S, BlockItem> itemConstructor) {
			this.itemConstructor = itemConstructor;
			return this;
		}

		public Builder<B, S> setItem(Function<B, BlockItem> itemConstructor) {
			this.itemConstructor = (block, type) -> itemConstructor.apply(block);
			return this;
		}

		public Builder<B, S> addType(S type) {
			subTypes.add(type);
			return this;
		}

		public Builder<B, S> addType(S[] types) {
			return addType(Arrays.asList(types));
		}

		public Builder<B, S> addType(Collection<S> types) {
			subTypes.addAll(types);
			return this;
		}

		public FeatureBlockGroup<B, S> create() {
			return new FeatureBlockGroup<>(this);
		}
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
