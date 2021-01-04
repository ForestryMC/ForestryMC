package forestry.modules.features;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.state.Property;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class FeatureBlock<B extends Block, I extends BlockItem> implements IBlockFeature<B, I> {
	protected final String moduleID;
	protected final String identifier;
	@Nullable
	private final Function<B, I> constructorItem;
	private final Supplier<B> constructorBlock;
	@Nullable
	private B block;
	@Nullable
	private I item;

	public FeatureBlock(
			String moduleID,
			String identifier,
			Supplier<B> constructorBlock,
			@Nullable Function<B, I> constructorItem
	) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.constructorBlock = constructorBlock;
		this.constructorItem = constructorItem;
	}

	@Override
	public boolean hasItem() {
		return item != null;
	}

	@Nullable
	@Override
	public I getItem() {
		return item;
	}

	@Override
	public void setItem(I item) {
		this.item = item;
	}

	@Override
	public boolean hasBlock() {
		return block != null;
	}

	@Nullable
	@Override
	public B getBlock() {
		return block;
	}

	@Override
	public void setBlock(B block) {
		this.block = block;
	}

	@Override
	public Supplier<B> getBlockConstructor() {
		return constructorBlock;
	}

	@Nullable
	@Override
	public Function<B, I> getItemBlockConstructor() {
		return constructorItem;
	}

	@Override
	public BlockState defaultState() {
		return block().getDefaultState();
	}

	@Override
	public <V extends Comparable<V>> BlockState with(Property<V> property, V value) {
		return defaultState().with(property, value);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public FeatureType getType() {
		return FeatureType.BLOCK;
	}

	@Override
	public String getModId() {
		return Constants.MOD_ID;
	}

	@Override
	public String getModuleId() {
		return moduleID;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		//TODO: Hacky, should find a better way
		if (block instanceof BlockBase) {
			((BlockBase) block).clientSetup();
		}
	}

	public String getTranslationKey() {
		return block == null ? "block." + Constants.MOD_ID + ":" + identifier : block.getTranslationKey();
	}
}
