package forestry.core.blocks;

import net.minecraft.util.IStringSerializable;

public interface IBlockType extends IStringSerializable {
	IMachineProperties<?> getMachineProperties();
}
