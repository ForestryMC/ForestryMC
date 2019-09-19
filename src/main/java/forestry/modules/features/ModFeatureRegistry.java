package forestry.modules.features;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
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
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;

public class ModFeatureRegistry {
	private static final HashMap<String, ModFeatureRegistry> registries = new LinkedHashMap<>();

	public static ModFeatureRegistry get(String modId) {
		return registries.computeIfAbsent(modId, ModFeatureRegistry::new);
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

	public void createObjects() {
		for (FeatureType type : FeatureType.values()) {
			modules.values().forEach(features -> features.createObjects(type));
			MinecraftForge.EVENT_BUS.post(new FeatureCreationEvent(modId, type));
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
		private final Multimap<FeatureType, Consumer<RegistryEvent>> listenerByType = LinkedListMultimap.create();
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
		public void addListener(FeatureType type, Consumer<RegistryEvent> listener) {
			listenerByType.put(type, listener);
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
				Supplier<Block> blockConstructor = feature.getConstructor();
				Block block = initObject(feature, ((IBlockFeature) feature).apply(blockConstructor.get()));
				block.setRegistryName(feature.getModId(), feature.getIdentifier());
				((IBlockFeature) feature).setBlock(block);
				Function<Block, Item> constructor = ((IBlockFeature) feature).getItemConstructor();
				if (constructor != null) {
					Item item = initObject(feature, ((IBlockFeature) feature).apply(constructor.apply(block)));
					if (item.getRegistryName() == null && block.getRegistryName() != null) {
						item.setRegistryName(block.getRegistryName());
					}
					((IBlockFeature) feature).setItem(item);
				}
			} else if (feature instanceof IItemFeature) {
				Item item = initObject(feature, ((IItemFeature) feature).apply((Item) feature.getConstructor().get()));
				item.setRegistryName(feature.getModId(), feature.getIdentifier());
				((IItemFeature) feature).setItem(item);
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

		@SuppressWarnings("unchecked")
		private <O, F extends IModFeature<O>> O initObject(F feature, O object) {
			if (object instanceof IFeatureObject) {
				((IFeatureObject<F>) object).init(feature);
			}
			feature.init();
			if (object instanceof IChildFeature) {
				IModFeature modFeature = (IModFeature) object;
				((IChildFeature) modFeature).setParent(feature);
				register(modFeature);
			}
			return object;
		}

		public <T extends IForgeRegistryEntry<T>> void onRegister(RegistryEvent.Register<T> event) {
			for (FeatureType type : FeatureType.values()) {
				for (IModFeature feature : featureByType.get(type)) {
					feature.register(event);
				}
				if (type.superType.isAssignableFrom(event.getRegistry().getRegistrySuperType())) {
					listenerByType.get(type).forEach(listener -> listener.accept(event));
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
