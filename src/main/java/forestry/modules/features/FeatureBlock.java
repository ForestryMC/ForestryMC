package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.state.IProperty;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;

public class FeatureBlock<B extends Block, I extends BlockItem> implements IBlockFeature<B, I> {
	private final String moduleID;
	private final String identifier;
	@Nullable
	private final Function<B, I> constructorItem;
	private final Supplier<B> constructorBlock;
	@Nullable
	private B block;
	@Nullable
	private I item;

	public FeatureBlock(String moduleID, String identifier, Supplier<B> constructorBlock, @Nullable Function<B, I> constructorItem) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.constructorBlock = constructorBlock;
		this.constructorItem = constructorItem;
	}

	@Override
	public void setItem(I item) {
		this.item = item;
	}

	@Override
	public boolean hasItem() {
		return item != null;
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

	@Nullable
	@Override
	public I getItem() {
		return item;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public Supplier<B> getConstructor() {
		return constructorBlock;
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
	public void setBlock(B block) {
		this.block = block;
	}

	@Override
	public BlockState defaultState() {
		return block().getDefaultState();
	}

	@Override
	public <V extends Comparable<V>> BlockState with(IProperty<V> property, V value) {
		return defaultState().with(property, value);
	}

	@Nullable
	@Override
	public Function<B, I> getItemConstructor() {
		return constructorItem;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		//TODO: Hacky, should find a better way
		if (block instanceof BlockBase) {
			((BlockBase) block).clientSetup();
		}
	}
}
