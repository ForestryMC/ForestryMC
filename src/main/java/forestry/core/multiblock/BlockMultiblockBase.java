package forestry.core.multiblock;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

/*
 * Base class for multiblock-capable blocks. This is only a reference implementation
 * and can be safely ignored.
 */
public abstract class BlockMultiblockBase extends BlockContainer {

	protected BlockMultiblockBase(Material material) {
		super(material);
	}
}
