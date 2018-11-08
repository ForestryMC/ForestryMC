package forestry.core.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

import forestry.core.utils.Log;
import forestry.core.utils.MigrationHelper;

public class TileEntityDataFixable implements IFixableData {
	@Override
	public int getFixVersion() {
		return 1;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String oldName = compound.getString("id").replace("minecraft:", "");
		if (oldName.startsWith("forestry.")) {
			String remappedName = MigrationHelper.getRemappedTileName(oldName);
			if (remappedName != null) {
				Log.debug("Replaced old Tile Entity name '{}' with the remapped name '{}'.", oldName, remappedName);
				compound.setString("id", remappedName);
			} else {
				Log.error("Failed to find remapped name for the Tile Entity with the name {}.", oldName);
			}
		}
		return compound;
	}
}
