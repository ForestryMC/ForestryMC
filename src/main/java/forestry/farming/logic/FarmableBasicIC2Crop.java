package forestry.farming.logic;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.vect.Vect;
import forestry.plugins.compat.PluginIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class FarmableBasicIC2Crop implements IFarmable {
	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		TileEntity crop = world.getTileEntity(x, y, z);
		if(PluginIC2.instance.isIC2Crop(crop)) {
			PluginIC2.instance.babysitCrop(crop);
			return true;
		}
		return false;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		TileEntity crop = world.getTileEntity(x, y, z);
		if (crop == null) {
			return null;
		}
		if (!PluginIC2.instance.isIC2Crop(crop)) {
			return null;
		}
		if (!PluginIC2.instance.canHarvestCrop(crop)) {
			return null;
		}
		return new CropBasicIC2Crop(world, crop, new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		return false;
	}
}
