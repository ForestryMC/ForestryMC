package forestry.core.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;

public class ItemLiquidContainerDrinkable extends ItemLiquidContainer {
	private final DrinkProperties properties;

	public ItemLiquidContainerDrinkable(EnumContainerType type, int color, DrinkProperties properties) {
		super(type, color);
		this.properties = properties;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
		stack.stackSize--;
		FoodStats foodStats = player.getFoodStats();
		foodStats.addStats(properties.getHealAmount(), properties.getSaturationModifier());
		world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
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
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (entityplayer.canEat(false)) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}
}
