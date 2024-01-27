package forestry.modules.features;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import forestry.api.core.ITileTypeProvider;
import net.minecraftforge.registries.RegisterEvent;

public interface ITileTypeFeature<T extends BlockEntity> extends IModFeature, ITileTypeProvider<T> {

	@Override
	default void create() {
        setTileType(getTileTypeConstructor().build(null));
	}

	@Override
	@SuppressWarnings("unchecked")
	default void register(RegisterEvent event) {
		if (hasTileType()) {
            event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY, new ResourceLocation(getModId(), getIdentifier()), this::tileType);
        }
	}

	@Override
	default BlockEntityType<T> tileType() {
		BlockEntityType<T> tileType = getTileType();
		if (tileType == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return tileType;
	}

	void setTileType(BlockEntityType<T> tileType);

	BlockEntityType.Builder<T> getTileTypeConstructor();
}
