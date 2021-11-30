package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;

import forestry.api.storage.IBackpackDefinition;
import net.minecraftforge.event.RegistryEvent;

import net.minecraftforge.fml.network.IContainerFactory;

import forestry.api.core.IBlockSubtype;
import forestry.api.core.IItemSubtype;
import forestry.api.storage.EnumBackpackType;

public interface IFeatureRegistry {

	<B extends Block, I extends BlockItem> FeatureBlock<B, I> block(Supplier<B> constructor, String identifier);

	<B extends Block, I extends BlockItem> FeatureBlock<B, I> block(Supplier<B> constructor, @Nullable Function<B, I> itemConstructor, String identifier);

	<B extends Block, S extends IBlockSubtype> FeatureBlockGroup.Builder<B, S> blockGroup(Function<S, B> constructor, Class<? extends S> typeClass);

	<B extends Block, S extends IBlockSubtype> FeatureBlockGroup.Builder<B, S> blockGroup(Function<S, B> constructor, Collection<S> types);

	<B extends Block, S extends IBlockSubtype> FeatureBlockGroup.Builder<B, S> blockGroup(Function<S, B> constructor, S[] types);

	<I extends Item> FeatureItem<I> item(Supplier<I> constructor, String identifier);

	FeatureItem<Item> backpack(IBackpackDefinition definition, EnumBackpackType type, String identifier);

	FeatureItem<Item> naturalistBackpack(IBackpackDefinition definition, String rootUid, ItemGroup tab, String identifier);

	<I extends Item, S extends IItemSubtype> FeatureItemGroup<I, S> itemGroup(Function<S, I> constructor, String identifier, S[] subTypes);

	<I extends Item, S extends IItemSubtype> FeatureItemGroup.Builder<I, S> itemGroup(Function<S, I> constructor, S[] subTypes);

	<I extends Item, R extends IItemSubtype, C extends IItemSubtype> FeatureItemTable<I, R, C> itemTable(BiFunction<R, C, I> constructor, R[] rowTypes, C[] columnTypes, String identifier);

	<I extends Item, R extends IItemSubtype, C extends IItemSubtype> FeatureItemTable.Builder<I, R, C> itemTable(BiFunction<R, C, I> constructor, R[] rowTypes, C[] columnTypes);

	<B extends Block, R extends IBlockSubtype, C extends IBlockSubtype> FeatureBlockTable.Builder<B, R, C> blockTable(BiFunction<R, C, B> constructor, R[] rowTypes, C[] columnTypes);

	<T extends TileEntity> FeatureTileType<T> tile(Supplier<T> constuctor, String identifier, Supplier<Collection<? extends Block>> validBlocks);

	<C extends Container> FeatureContainerType<C> container(IContainerFactory<C> factory, String identifier);

	<E extends Entity> FeatureEntityType<E> entity(EntityType.IFactory<E> factory, EntityClassification classification, String identifier);

	<E extends Entity> FeatureEntityType<E> entity(EntityType.IFactory<E> factory, EntityClassification classification, String identifier, UnaryOperator<EntityType.Builder<E>> consumer);

	<E extends Entity> FeatureEntityType<E> entity(EntityType.IFactory<E> factory, EntityClassification classification, String identifier, UnaryOperator<EntityType.Builder<E>> consumer, Supplier<AttributeModifierMap.MutableAttribute> attributes);

	FeatureFluid.Builder fluid(String identifier);

	void addRegistryListener(FeatureType type, Consumer<RegistryEvent> listener);

	<F extends IModFeature> F register(F feature);

	Collection<IModFeature> getFeatures();

	Collection<IModFeature> getFeatures(FeatureType type);
}
