package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import forestry.core.config.Constants;

public class FeatureTileType<T extends BlockEntity> implements ITileTypeFeature<T> {
	protected final String moduleID;
	protected final String identifier;
	private final Supplier<T> constructorTileEntity;
	@Nullable
	private BlockEntityType<T> tileType;
	private Supplier<Collection<? extends Block>> validBlocks;

	public FeatureTileType(String moduleID, String identifier, Supplier<T> constructorTileEntity, Supplier<Collection<? extends Block>> validBlocks) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.constructorTileEntity = constructorTileEntity;
		this.validBlocks = validBlocks;
	}


	@Override
	public boolean hasTileType() {
		return tileType != null;
	}

	@Nullable
	@Override
	public BlockEntityType<T> getTileType() {
		return tileType;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public FeatureType getType() {
		return FeatureType.TILE;
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
	public void setTileType(BlockEntityType<T> tileType) {
		this.tileType = tileType;
	}

	@Override
	public BlockEntityType.Builder<T> getTileTypeConstructor() {
		return BlockEntityType.Builder.of(constructorTileEntity, validBlocks.get().toArray(new Block[0]));
	}
}
