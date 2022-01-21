package forestry.apiculture.items;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.hives.IHiveTile;
import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class ItemSmoker extends ItemForestry {
	public ItemSmoker() {
		super((new Item.Properties())
				.stacksTo(1)
				.tab(ItemGroups.tabApiculture));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (worldIn.isClientSide && isSelected && worldIn.random.nextInt(40) == 0) {
			addSmoke(stack, worldIn, entityIn, 1);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		super.onUsingTick(stack, player, count);
		Level world = player.level;
		addSmoke(stack, world, player, (count % 5) + 1);
	}

	private static HumanoidArm getHandSide(ItemStack stack, Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity LivingEntity = (LivingEntity) entity;
			InteractionHand activeHand = LivingEntity.getUsedItemHand();
			HumanoidArm handSide = LivingEntity.getMainArm();
			if (activeHand == InteractionHand.OFF_HAND) {
				// TODO: use EnumHandSide.opposite() when it's no longer client-only
				handSide = handSide == HumanoidArm.LEFT ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
			}
			return handSide;
		}
		return HumanoidArm.RIGHT;
	}

	private static void addSmoke(ItemStack stack, Level world, Entity entity, int distance) {
		if (distance <= 0) {
			return;
		}
		Vec3 look = entity.getLookAngle();
		HumanoidArm handSide = getHandSide(stack, entity);

		Vec3 handOffset;
		if (handSide == HumanoidArm.RIGHT) {
			handOffset = look.cross(new Vec3(0, 1, 0));
		} else {
			handOffset = look.cross(new Vec3(0, -1, 0));
		}

		Vec3 lookDistance = new Vec3(look.x * distance, look.y * distance, look.z * distance);
		Vec3 scaledOffset = handOffset.scale(1.0 / distance);
		Vec3 smokePos = lookDistance.add(entity.position()).add(scaledOffset);

		if (world.isClientSide) {
			ParticleRender.addEntitySmokeFX(world, smokePos.x, smokePos.y + 1, smokePos.z);
		}

		BlockPos blockPos = new BlockPos(smokePos.x, smokePos.y + 1, smokePos.z);
		TileUtil.actOnTile(world, blockPos, IHiveTile.class, IHiveTile::calmBees);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		ItemStack itemStack = playerIn.getItemInHand(handIn);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		TileUtil.actOnTile(context.getLevel(), context.getClickedPos(), IHiveTile.class, IHiveTile::calmBees);
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ICapabilityProvider() {

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					return LazyOptional.of(capability::getDefaultInstance);
				}
				return LazyOptional.empty();
			}
		};
	}
}
