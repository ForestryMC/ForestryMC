package forestry.farming.logic.farmables;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.farming.logic.crops.CropBasicIC2Crop;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

//import net.minecraftforge.fml.common.Optional;
//import forestry.plugins.PluginIC2;

//import ic2.api.crops.ICropTile;

public class FarmableBasicIC2Crop implements IFarmable {

    /**
     * Perform some of the actions of the crop-matron.
     */
    //	@Optional.Method(modid = PluginIC2.MOD_ID)
    //	public static void babysitCrop(TileEntity tileEntity) {
    //		if (CropBasicIC2Crop.isIC2Crop(tileEntity)) {
    //			ICropTile crop = (ICropTile) tileEntity;
    //			/*
    //			This part might be unbalanced until a custom farm logic is added and makes use of weed-ex.
    //			if (crop.getCrop() != null) {
    //				if (crop.getCrop().isWeed(crop)) {
    //					crop.reset();
    //				}
    //			}*/
    //			if (crop.getStorageWater() <= 200) {
    //				crop.setStorageWater(200);
    //			}
    //			if (crop.getStorageNutrients() <= 100) {
    //				crop.setStorageNutrients(crop.getStorageNutrients() + 100);
    //			}
    //		}
    //	}
    @Override
    public boolean isSaplingAt(World world, BlockPos pos, BlockState blockState) {
        TileEntity crop = world.getTileEntity(pos);
        //CropBasicIC2Crop.isIC2Crop(crop)) {
        //			babysitCrop(crop);
        return false;
    }

    @Nullable
    @Override
    public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
        TileEntity crop = world.getTileEntity(pos);
        if (crop == null) {
            return null;
        }
        if (true) {//!CropBasicIC2Crop.isIC2Crop(crop)) {
            return null;
        }
        if (true) {//!CropBasicIC2Crop.canHarvestCrop(crop)) {
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
    public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
        return false;
    }
}
