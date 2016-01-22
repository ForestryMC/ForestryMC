/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.food.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.config.Config;
import forestry.core.items.ItemForestryFood;
import forestry.core.items.ItemOverlay;
import forestry.food.BeverageEffect;

public class ItemBeverage extends ItemForestryFood {
	public interface IBeverageInfo extends ItemOverlay.IOverlayInfo {
		int getHeal();

		float getSaturation();

		boolean isAlwaysEdible();

		void registerModels(Item item, IModelManager manager);
	}

	public final IBeverageInfo[] beverages;

	public ItemBeverage() {
		super(1, 0.2f);
		setMaxStackSize(1);
		this.beverages = EnumBeverage.VALUES;
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(stack);

		stack.stackSize--;
		player.getFoodStats().addStats(this, stack);
		world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

		if (world.isRemote) {
			return stack;
		}

		for (IBeverageEffect effect : effects) {
			effect.doEffect(world, player);
		}

		return stack;
	}

	@Override
	public int getHealAmount(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];
		return beverage.getHeal();
	}

	@Override
	public float getSaturationModifier(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];
		return beverage.getSaturation();
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.DRINK;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];

		if (entityplayer.canEat(beverage.isAlwaysEdible())) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < beverages.length; i++) {
			if (Config.isDebug || !beverages[i].isSecret()) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(itemstack);

		for (IBeverageEffect effect : effects) {
			if (effect.getDescription() != null) {
				list.add(effect.getDescription());
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + beverages[stack.getItemDamage()].getName();
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (IBeverageInfo info : beverages) {
			info.registerModels(item, manager);
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || beverages[itemstack.getItemDamage()].getSecondaryColor() == 0) {
			return beverages[itemstack.getItemDamage()].getPrimaryColor();
		} else {
			return beverages[itemstack.getItemDamage()].getSecondaryColor();
		}
	}

	public ItemStack get(EnumBeverage beverage, int amount) {
		return new ItemStack(this, amount, beverage.ordinal());
	}
}
