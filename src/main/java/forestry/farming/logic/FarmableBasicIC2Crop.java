package forestry.farming.logic;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.plugins.compat.PluginIC2;

import ic2.api.crops.ICropTile;

public class FarmableBasicIC2Crop implements IFarmable {

	/**
	 * Perform some of the actions of the crop-matron.
	 */
	@Optional.Method(modid = PluginIC2.modId)
	public static void babysitCrop(TileEntity tileEntity) {
		if (CropBasicIC2Crop.isIC2Crop(tileEntity)) {
			ICropTile crop = (ICropTile) tileEntity;
			/*
			This part might be unbalanced until a custom farm logic is added and makes use of weed-ex.
			if (crop.getCrop() != null) {
				if (crop.getCrop().isWeed(crop)) {
					crop.reset();
				}
			}*/
			if (crop.getStorageWater() <= 200) {
				crop.setStorageWater(200);
			}
			if (crop.getStorageNutrient() <= 100) {
				crop.setStorageNutrient(crop.getStorageNutrient() + 100);
			}
		}
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		TileEntity crop = world.getTileEntity(pos);
		if (CropBasicIC2Crop.isIC2Crop(crop)) {
			babysitCrop(crop);
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		TileEntity crop = world.getTileEntity(pos);
		if (crop == null) {
			return null;
		}
		if (!CropBasicIC2Crop.isIC2Crop(crop)) {
			return null;
		}
		if (!CropBasicIC2Crop.canHarvestCrop(crop)) {
			return null;
		}
		return new CropBasicIC2Crop(world, crop, pos);
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
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return false;
	}
}
