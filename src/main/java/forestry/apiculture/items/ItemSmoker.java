package forestry.apiculture.items;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.IHiveTile;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class ItemSmoker extends ItemForestry {
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
		World world = player.world;
		addSmoke(stack, world, player, (count % 5) + 1);
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

	private static void addSmoke(ItemStack stack, World world, Entity entity, int distance) {
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

		Vec3d lookDistance = new Vec3d(look.x * distance, look.y * distance, look.z * distance);
		Vec3d scaledOffset = handOffset.scale(1.0 / distance);
		Vec3d smokePos = lookDistance.add(entity.getPositionVector()).add(scaledOffset);

		if (world.isRemote) {
			ParticleRender.addEntitySmokeFX(world, smokePos.x, smokePos.y + 1, smokePos.z);
		}

		BlockPos blockPos = new BlockPos(smokePos.x, smokePos.y + 1, smokePos.z);
		TileUtil.actOnTile(world, blockPos, IHiveTile.class, IHiveTile::calmBees);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileUtil.actOnTile(world, pos, IHiveTile.class, IHiveTile::calmBees);
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == ApicultureCapabilities.ARMOR_APIARIST;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					return capability.getDefaultInstance();
				}
				return null;
			}
		};
	}
}
