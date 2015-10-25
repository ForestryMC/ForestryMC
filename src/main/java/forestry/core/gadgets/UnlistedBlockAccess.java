package forestry.core.gadgets;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedBlockAccess implements IUnlistedProperty<IBlockAccess> {
	public static UnlistedBlockAccess BLOCKACCESS = new UnlistedBlockAccess();

	@Override
	public String getName() {
		return "ba";
	}

	@Override
	public boolean isValid(final IBlockAccess value) {
		return true;
	}

	@Override
	public Class<IBlockAccess> getType() {
		return IBlockAccess.class;
	}

	@Override
	public String valueToString(final IBlockAccess value) {
		return null;
	}
}