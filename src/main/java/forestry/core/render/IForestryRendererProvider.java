package forestry.core.render;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Constructor to register IForestryRenderer
 *
 * @param <T> The type of the tile entity
 */
@FunctionalInterface
public interface IForestryRendererProvider<T extends BlockEntity> {
	IForestryRenderer<? super T> create(ModelPart root);
}
