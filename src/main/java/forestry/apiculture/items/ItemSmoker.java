package forestry.apiculture.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
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
	public ItemSmoker() {
		super(Tabs.tabApiculture);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (worldIn.isRemote && isSelected && worldIn.rand.nextInt(40) == 0) {
			addSmoke(stack, worldIn, entityIn, 1);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		super.onUsingTick(stack, player, count);
		World worldObj = player.worldObj;
		addSmoke(stack, worldObj, player, (count % 5) + 1);
	}

	private static EnumHandSide getHandSide(ItemStack stack, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
			EnumHand activeHand = entityLivingBase.getActiveHand();
			EnumHandSide handSide = entityLivingBase.getPrimaryHand();
			if (activeHand == EnumHand.OFF_HAND) {
				// TODO: use EnumHandSide.opposite() when it's no longer client-only
				handSide = handSide == EnumHandSide.LEFT ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
			}
			return handSide;
		}
		return EnumHandSide.RIGHT;
	}

	private static void addSmoke(ItemStack stack, World worldObj, Entity entity, int distance) {
		if (distance <= 0) {
			return;
		}
		Vec3d look = entity.getLookVec();
		EnumHandSide handSide = getHandSide(stack, entity);

		Vec3d handOffset;
		if (handSide == EnumHandSide.RIGHT) {
			handOffset = look.crossProduct(new Vec3d(0, 1, 0));
		} else {
			handOffset = look.crossProduct(new Vec3d(0, -1, 0));
		}

		Vec3d lookDistance = new Vec3d(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
		Vec3d scaledOffset = handOffset.scale(1.0 / distance);
		Vec3d smokePos = lookDistance.add(entity.getPositionVector()).add(scaledOffset);

		Proxies.render.addEntitySmokeFX(worldObj, smokePos.xCoord, smokePos.yCoord + 1, smokePos.zCoord);
		BlockPos blockPos = new BlockPos(smokePos.xCoord, smokePos.yCoord + 1, smokePos.zCoord);
		TileEntity tileEntity = worldObj.getTileEntity(blockPos);
		if (tileEntity instanceof IHiveTile) {
			IHiveTile hive = (IHiveTile) tileEntity;
			hive.calmBees();
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof IHiveTile) {
			IHiveTile hive = (IHiveTile) tileEntity;
			hive.calmBees();
		}
		return super.onItemUseFirst(stack, player, world, pos, side, hitX, hitY, hitZ, hand);
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
