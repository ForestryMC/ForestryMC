package forestry.core.items;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.IModelManager;
import forestry.core.gui.ContainerCamouflageSprayCan;
import forestry.core.gui.GuiCamouflageSprayCan;
import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import forestry.core.tiles.TileUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCamouflageSprayCan extends ItemWithGui {

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiCamouflageSprayCan(player, new ItemInventoryCamouflageSprayCan(player, heldItem));
	}

	@Override
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerCamouflageSprayCan(new ItemInventoryCamouflageSprayCan(player, heldItem), player.inventory);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		super.registerModel(item, manager);
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:camouflage_spray_can_filled"));
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {
		ItemStack heldItem = player.getHeldItem(hand);
		ItemInventoryCamouflageSprayCan inventory = new ItemInventoryCamouflageSprayCan(player, heldItem);
		ItemStack camouflage = inventory.getStackInSlot(0);
		ICamouflageHandler handler = TileUtil.getTile(world, pos, ICamouflageHandler.class);
		if (handler != null) {
			String type = CamouflageManager.camouflageAccess.getHandlerFromItem(camouflage).getType();
			if (handler.canHandleType(type)) {
				if(world.isRemote){
					handler.setCamouflageBlock(type, camouflage, true);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!worldIn.isRemote && playerIn.isSneaking()) {
			openGui(playerIn);
		}

		ItemStack heldItem = playerIn.getHeldItem(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
	}

}
