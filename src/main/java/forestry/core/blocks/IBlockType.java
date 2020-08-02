package forestry.core.blocks;

import forestry.api.core.IBlockSubtype;

public interface IBlockType extends IBlockSubtype {
    IMachineProperties<?> getMachineProperties();
}
