package forestry.core.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemLiquidContainerDrinkable extends ItemLiquidContainer {
	private final DrinkProperties properties;

	public ItemLiquidContainerDrinkable(EnumContainerType type, int color, DrinkProperties properties) {
		super(type, color);
		this.properties = properties;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer && !((EntityPlayer)entityLiving).capabilities.isCreativeMode) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
			}

			if (!worldIn.isRemote) {
				FoodStats foodStats = player.getFoodStats();
				foodStats.addStats(properties.getHealAmount(), properties.getSaturationModifier());
				worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			}

			player.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return properties.getMaxItemUseDuration();
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (playerIn.canEat(false)) {
			playerIn.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
		}
	}
}
