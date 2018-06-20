package forestry.core.tiles;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;

import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;

import net.minecraftforge.fml.common.FMLCommonHandler;

import forestry.core.config.Constants;
import forestry.core.utils.Log;

public class TileEntityDataFixer {

	public TileEntityDataFixer() {
		Fixable fixable = new Fixable();
		CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
		ModFixs modFixs = fixer.init(Constants.MOD_ID, fixable.getFixVersion());    //is there a current save format version?
		modFixs.registerFix(FixTypes.BLOCK_ENTITY, fixable);
	}

	public static class Fixable implements IFixableData {

		@Override
		public int getFixVersion() {
			return 1;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
			String oldName = compound.getString("id");
			if (oldName.startsWith("forestry.")) {
				String id = oldName.replace("forestry.", "");
				WordUtils.uncapitalize(id);
				id = "forestry:" + id;
				Log.info("Replaced bad TE name {} with {}", oldName, id);
				compound.setString("id", id);
			}
			return compound;
		}
	}
}
