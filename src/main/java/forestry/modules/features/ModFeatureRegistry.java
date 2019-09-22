package forestry.modules.features;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IBlockSubtype;
import forestry.api.core.IItemSubtype;
import forestry.api.modules.ForestryModule;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;

public class ModFeatureRegistry {
	private static final HashMap<String, ModFeatureRegistry> registries = new LinkedHashMap<>();

	public static ModFeatureRegistry get(String modId) {
		return registries.computeIfAbsent(modId, ModFeatureRegistry::new);
	}

	public static IFeatureRegistry get(Class<?> clazz) {
		ForestryModule module = clazz.getAnnotation(ForestryModule.class);
		if (module != null) {
			return get(module.containerID()).getRegistry(module.moduleID());
		}
		return get(Constants.MOD_ID).getRegistry(ForestryModuleUids.CORE);
	}

	public IFeatureRegistry getRegistry(String moduleID) {
		return modules.computeIfAbsent(moduleID, ModuleFeatures::new);
	}

	private final HashMap<String, ModuleFeatures> modules = new LinkedHashMap<>();
	private final String modId;

	private ModFeatureRegistry(String modId) {
		this.modId = modId;
	}

	public static void fireEvent() {
		RegisterFeatureEvent event = new RegisterFeatureEvent();
		MinecraftForge.EVENT_BUS.post(event);
	}

	public void register(IModFeature feature) {
		modules.computeIfAbsent(feature.getModuleId(), ModuleFeatures::new).register(feature);
	}

	public boolean isEnabled(IModFeature feature) {
		return ForestryAPI.moduleManager.isModuleEnabled(modId, feature.getModuleId());
	}

	public void createObjects(BiPredicate<FeatureType, String> filter) {
		for (FeatureType type : FeatureType.values()) {
			modules.values().forEach(features -> {
				if (filter.test(type, features.moduleID)) {
					features.createObjects(type);
					MinecraftForge.EVENT_BUS.post(new FeatureCreationEvent(modId, features.moduleID, type));
				}
			});
		}
	}

	public <T extends IForgeRegistryEntry<T>> void onRegister(RegistryEvent.Register<T> event) {
		for (ModuleFeatures features : modules.values()) {
			features.onRegister(event);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		for (ModuleFeatures features : modules.values()) {
			features.clientSetup();
		}
	}

	private static class ModuleFeatures implements IFeatureRegistry {
		private final HashMap<String, IModFeature> featureById = new LinkedHashMap<>();
		private final Multimap<FeatureType, IModFeature> featureByType = LinkedListMultimap.create();
		private final Multimap<FeatureType, Consumer<RegistryEvent>> registryListeners = LinkedListMultimap.create();
		private final String moduleID;

		public ModuleFeatures(String moduleID) {
			this.moduleID = moduleID;
		}

		@Override
		public <B extends Block, I extends BlockItem> FeatureBlock<B, I> block(Supplier<B> constructor, String identifier) {
			return block(constructor, null, identifier);
		}

		@Override
		public <B extends Block, I extends BlockItem> FeatureBlock<B, I> block(Supplier<B> constructor, @Nullable Function<B, I> itemConstructor, String identifier) {
			return register(new FeatureBlock<>(moduleID, identifier, constructor, itemConstructor));
		}

		@Override
		public <B extends Block, S extends IBlockSubtype> FeatureBlockGroup.Builder<B, S> blockGroup(Function<S, B> constructor, Class<? extends S> typeClass) {
			return new FeatureBlockGroup.Builder<>(this, constructor);
		}

		@Override
		public <B extends Block, S extends IBlockSubtype> FeatureBlockGroup.Builder<B, S> blockGroup(Function<S, B> constructor, Collection<S> types) {
			return (FeatureBlockGroup.Builder<B, S>) new FeatureBlockGroup.Builder<>(this, constructor).types(types);
		}

		@Override
		public <B extends Block, S extends IBlockSubtype> FeatureBlockGroup.Builder<B, S> blockGroup(Function<S, B> constructor, S[] types) {
			return (FeatureBlockGroup.Builder<B, S>) new FeatureBlockGroup.Builder<>(this, constructor).types(types);
		}

		@Override
		public <I extends Item> FeatureItem<I> item(Supplier<I> constructor, String identifier) {
			return register(new FeatureItem<>(moduleID, identifier, constructor));
		}

		@Override
		public FeatureItem<Item> backpack(String backpackUid, EnumBackpackType type, String identifier) {
			return item(() -> BackpackManager.backpackInterface.createBackpack(backpackUid, type), identifier);
		}

		@Override
		public FeatureItem<Item> naturalistBackpack(String backpackUid, String rootUid, ItemGroup tab, String identifier) {
			return item(() -> BackpackManager.backpackInterface.createNaturalistBackpack(backpackUid, rootUid, tab), identifier);
		}

		@Override
		public <I extends Item, S extends IItemSubtype> FeatureItemGroup<I, S> itemGroup(Function<S, I> constructor, String identifier, S[] subTypes) {
			return itemGroup(constructor, subTypes).identifier(identifier).create();
		}

		@Override
		public <I extends Item, S extends IItemSubtype> FeatureItemGroup.Builder<I, S> itemGroup(Function<S, I> constructor, S[] subTypes) {
			return (FeatureItemGroup.Builder<I, S>) new FeatureItemGroup.Builder<>(this, constructor).types(subTypes);
		}

		@Override
		public <I extends Item, R extends IItemSubtype, C extends IItemSubtype> FeatureItemTable<I, R, C> itemTable(BiFunction<R, C, I> constructor, R[] rowTypes, C[] columnTypes, String identifier) {
			return itemTable(constructor, rowTypes, columnTypes).identifier(identifier).create();
		}

		@Override
		public <I extends Item, R extends IItemSubtype, C extends IItemSubtype> FeatureItemTable.Builder<I, R, C> itemTable(BiFunction<R, C, I> constructor, R[] rowTypes, C[] columnTypes) {
			return (FeatureItemTable.Builder<I, R, C>) new FeatureItemTable.Builder<>(this, constructor).rowTypes(rowTypes).columnTypes(columnTypes);
		}

		@Override
		public <B extends Block, R extends IBlockSubtype, C extends IBlockSubtype> FeatureBlockTable.Builder<B, R, C> blockTable(BiFunction<R, C, B> constructor, R[] rowTypes, C[] columnTypes) {
			return (FeatureBlockTable.Builder<B, R, C>) new FeatureBlockTable.Builder<>(this, constructor).rowTypes(rowTypes).columnTypes(columnTypes);
		}

		@Override
		public FeatureFluid.Builder fluid(String identifier) {
			return new FeatureFluid.Builder(this, moduleID, identifier);
		}

		@Override
		public void addRegistryListener(FeatureType type, Consumer<RegistryEvent> listener) {
			registryListeners.put(type, listener);
		}

		public <F extends IModFeature> F register(F feature) {
			featureById.put(feature.getIdentifier(), feature);
			featureByType.put(feature.getType(), feature);
			return feature;
		}

		public IModFeature getFeature(String identifier) {
			return featureById.get(identifier);
		}

		public void createObjects(FeatureType type) {
			for (IModFeature feature : featureByType.get(type)) {
				createObject(feature);
			}
		}

		@SuppressWarnings("unchecked")
		private void createObject(IModFeature feature) {
			if (!feature.isEnabled()) {
				return;
			}
			if (feature instanceof IBlockFeature) {
				IBlockFeature blockFeature = (IBlockFeature<?, ?>) feature;
				Supplier<Block> blockConstructor = blockFeature.getBlockConstructor();
				Block block = blockConstructor.get();
				block.setRegistryName(feature.getModId(), feature.getIdentifier());
				blockFeature.setBlock(block);
				Function<Block, Item> constructor = blockFeature.getItemBlockConstructor();
				if (constructor != null) {
					Item item = constructor.apply(block);
					if (item.getRegistryName() == null && block.getRegistryName() != null) {
						item.setRegistryName(block.getRegistryName());
					}
					blockFeature.setItem(item);
				}
			} else if (feature instanceof IItemFeature) {
				IItemFeature itemFeature = (IItemFeature<?>) feature;
				Item item = (Item) itemFeature.getItemConstructor().get();
				item.setRegistryName(feature.getModId(), feature.getIdentifier());
				itemFeature.setItem(item);
			} else if (feature instanceof IFluidFeature) {
				IFluidFeature fluidFeature = (IFluidFeature) feature;
				FlowingFluid fluid = fluidFeature.getFluidConstructor(false).get();
				FlowingFluid flowing = fluidFeature.getFluidConstructor(true).get();
				fluid.setRegistryName(feature.getModId(), feature.getIdentifier());
				flowing.setRegistryName(feature.getModId(), feature.getIdentifier() + "_flowing");
				fluidFeature.setFluid(fluid);
				fluidFeature.setFlowing(flowing);
			}
			//			if (feature instanceof IMachineFeature) {
			//				MachineGroup group = initObject(feature, ((IMachineFeature) feature).apply(((IMachineFeature) feature).getConstructor().createObject()));
			//				((IMachineFeature) feature).setGroup(group);
			//			}
			//			if (feature instanceof IFluidDefinition) {
			//				FluidType fluid = initObject(feature, ((IFluidDefinition) feature).apply(((IFluidDefinition) feature).getConstructor().createObject()));
			//				((IFluidDefinition) feature).setFluid(fluid);
			//			}

		}

		public <T extends IForgeRegistryEntry<T>> void onRegister(RegistryEvent.Register<T> event) {
			for (FeatureType type : FeatureType.values()) {
				for (IModFeature feature : featureByType.get(type)) {
					feature.register(event);
				}
				if (type.superType.isAssignableFrom(event.getRegistry().getRegistrySuperType())) {
					registryListeners.get(type).forEach(listener -> listener.accept(event));
				}
			}
		}

		@OnlyIn(Dist.CLIENT)
		public void clientSetup() {
			for (IModFeature feature : featureByType.values()) {
				feature.clientSetup();
			}
		}

	}
}
