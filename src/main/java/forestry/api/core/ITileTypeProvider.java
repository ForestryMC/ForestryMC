package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ITileTypeProvider<T extends BlockEntity> {
	boolean hasTileType();

	@Nullable
	BlockEntityType<T> getTileType();

	BlockEntityType<T> tileType();


}
