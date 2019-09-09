package genetics.root;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.translator.IBlockTranslator;
import genetics.api.root.translator.IIndividualTranslator;
import genetics.api.root.translator.IItemTranslator;

public class IndividualTranslator<I extends IIndividual> implements IIndividualTranslator<I> {
	private final IIndividualRoot<I> root;
	private final Map<Item, IItemTranslator<I>> itemTranslators = new HashMap<>();
	private final Map<Block, IBlockTranslator<I>> blockTranslators = new HashMap<>();

	public IndividualTranslator(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public IIndividualRoot<I> getRoot() {
		return root;
	}

	@Override
	public IIndividualTranslator<I> registerTranslator(IBlockTranslator<I> translator, Block... translatorKeys) {
		for (Block key : translatorKeys) {
			blockTranslators.put(key, translator);
		}
		return this;
	}

	@Override
	public IIndividualTranslator<I> registerTranslator(IItemTranslator<I> translator, Item... translatorKeys) {
		for (Item key : translatorKeys) {
			itemTranslators.put(key, translator);
		}
		return this;
	}

	@Override
	public Optional<IItemTranslator<I>> getTranslator(Item translatorKey) {
		return Optional.ofNullable(itemTranslators.get(translatorKey));
	}

	@Override
	public Optional<IBlockTranslator<I>> getTranslator(Block translatorKey) {
		return Optional.ofNullable(blockTranslators.get(translatorKey));
	}

	@Override
	public Optional<I> translateMember(BlockState objectToTranslate) {
		Optional<IBlockTranslator<I>> optional = getTranslator(objectToTranslate.getBlock());
		return optional.map(iiBlockTranslator -> iiBlockTranslator.getIndividualFromObject(objectToTranslate));
	}

	@Override
	public Optional<I> translateMember(ItemStack objectToTranslate) {
		Optional<IItemTranslator<I>> optional = getTranslator(objectToTranslate.getItem());
		return optional.map(iiItemTranslator -> iiItemTranslator.getIndividualFromObject(objectToTranslate));
	}

	@Override
	public ItemStack getGeneticEquivalent(BlockState objectToTranslate) {
		Optional<IBlockTranslator<I>> optional = getTranslator(objectToTranslate.getBlock());
		return optional.map(blockTranslator -> blockTranslator.getGeneticEquivalent(objectToTranslate)).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack getGeneticEquivalent(ItemStack objectToTranslate) {
		Optional<IItemTranslator<I>> optional = getTranslator(objectToTranslate.getItem());
		return optional.map(itemTranslator -> itemTranslator.getGeneticEquivalent(objectToTranslate)).orElse(ItemStack.EMPTY);
	}

	@Override
	public ComponentKey<IIndividualTranslator> getKey() {
		return ComponentKeys.TRANSLATORS;
	}
}
