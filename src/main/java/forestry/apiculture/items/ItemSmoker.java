package forestry.apiculture.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.IHiveTile;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Translator;

public class ItemSmoker extends ItemForestry implements IArmorApiarist {
	private static final int RANGE = 5;

	public ItemSmoker() {
		super(Tabs.tabApiculture);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (worldIn.isRemote && isSelected && worldIn.rand.nextInt(40) == 0) {
			addSmoke(worldIn, entityIn, 1);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		super.onUsingTick(stack, player, count);
		World worldObj = player.worldObj;
		addSmoke(worldObj, player, count % RANGE);
	}

	private static void addSmoke(World worldObj, Entity entity, int distance) {
		Vec3d look = entity.getLookVec();
		Vec3d handOffset = look.crossProduct(new Vec3d(0, 1, 0));
		Vec3d lookDistance = new Vec3d(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
		Vec3d smokePos = lookDistance.add(entity.getPositionVector()).add(handOffset);
		Proxies.render.addEntitySmokeFX(worldObj, smokePos.xCoord, smokePos.yCoord + 1, smokePos.zCoord);
		BlockPos blockPos = new BlockPos(smokePos.xCoord, smokePos.yCoord + 1, smokePos.zCoord);
		TileEntity tileEntity = worldObj.getTileEntity(blockPos);
		if (tileEntity instanceof IHiveTile) {
			IHiveTile hive = (IHiveTile) tileEntity;
			hive.calmBees();
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
		return itemStackIn;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof IHiveTile) {
			IHiveTile hive = (IHiveTile) tileEntity;
			hive.calmBees();
		}
		return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(Translator.translateToLocal("item.for.smoker.description"));
	}

	@Override
	public boolean protectEntity(EntityLivingBase entity, ItemStack armor, String cause, boolean doProtect) {
		return true;
	}
}
