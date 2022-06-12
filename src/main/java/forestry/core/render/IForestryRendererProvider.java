package forestry.core.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Constructor to register IForestryRenderer
 *
 * @param <T> The type of the tile entity
 */
@FunctionalInterface
public interface IForestryRendererProvider<T extends BlockEntity> {
	IForestryRenderer<? super T> create(BlockEntityRendererProvider.Context ctx);
}
