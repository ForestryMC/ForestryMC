package forestry.core.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;

import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;

import net.minecraftforge.fml.common.FMLCommonHandler;

import forestry.core.config.Constants;

public class TileEntityDataFixer {
	//id tag is the name.
	private CompoundDataFixer fixer;
	private ModFixs modFixs;
	private Fixable fixable;

	public TileEntityDataFixer() {
		fixable = new Fixable();
		fixer = FMLCommonHandler.instance().getDataFixer();
		modFixs = fixer.init(Constants.MOD_ID, fixable.getFixVersion());    //is there a current save format version?
		modFixs.registerFix(FixTypes.BLOCK_ENTITY, fixable);
	}

	public static class Fixable implements IFixableData {

		@Override
		public int getFixVersion() {
			return 1;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
			String id = compound.getString("id");
			if (id.contains("forestry.")) {
				String teName = id.split("\\.")[1];
				id = "forestry:" + teName;
			}
			compound.setString("id", id);
			return compound;
		}
	}
}
