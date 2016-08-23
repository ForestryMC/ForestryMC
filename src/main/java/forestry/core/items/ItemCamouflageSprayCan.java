package forestry.core.items;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.IModelManager;
import forestry.core.gui.ContainerCamouflageSprayCan;
import forestry.core.gui.GuiCamouflageSprayCan;
import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCamouflageSprayCan extends ItemWithGui {
	
	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiCamouflageSprayCan(player, new ItemInventoryCamouflageSprayCan(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerCamouflageSprayCan(new ItemInventoryCamouflageSprayCan(player, heldItem), player.inventory);
	}
	
	@Override
	public void registerModel(Item item, IModelManager manager) {
		super.registerModel(item, manager);
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:camouflageSprayCanFilled"));
	}
	
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemInventoryCamouflageSprayCan inventory = new ItemInventoryCamouflageSprayCan(player, stack);
		ItemStack camouflage = inventory.getStackInSlot(0);
		if(camouflage != null){
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof ICamouflageHandler){
				ICamouflageHandler handler = (ICamouflageHandler) tile;
				String type = CamouflageManager.camouflageAccess.getHandlerFromItem(camouflage).getType();
				if(type != null && handler.canHandleType(type)){
					if(world.isRemote){
						handler.setCamouflageBlock(type, camouflage);
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote && playerIn.isSneaking()) {
			openGui(playerIn);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
	}

}
