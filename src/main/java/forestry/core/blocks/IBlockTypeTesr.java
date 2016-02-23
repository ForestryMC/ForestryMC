package forestry.core.blocks;

import javax.annotation.Nonnull;

public interface IBlockTypeTesr extends IBlockType {
	@Nonnull
	IMachinePropertiesTesr<?> getMachineProperties();
}
