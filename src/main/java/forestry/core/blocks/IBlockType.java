package forestry.core.blocks;

import javax.annotation.Nonnull;

import net.minecraft.util.IStringSerializable;

public interface IBlockType extends IStringSerializable {
	@Nonnull
	IMachineProperties<?> getMachineProperties();
}
