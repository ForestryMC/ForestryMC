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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.config.Config;
import forestry.core.items.ItemForestryFood;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

public class ItemBeverage extends ItemForestryFood {

	public static class BeverageInfo {

		public final String name;
		private final String iconType;
		public final int primaryColor;
		public final int secondaryColor;

		public final int heal;
		public final float saturation;
		public final boolean isAlwaysEdible;

		public boolean isSecret = false;

		public BeverageInfo(String name, String iconType, int primaryColor, int secondaryColor, int heal, float saturation, boolean isAlwaysEdible) {
			this.name = name;
			this.iconType = iconType;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
			this.heal = heal;
			this.saturation = saturation;
			this.isAlwaysEdible = isAlwaysEdible;
		}

		public static List<IBeverageEffect> loadEffects(ItemStack stack) {
			List<IBeverageEffect> effectsList = Collections.emptyList();

			NBTTagCompound nbttagcompound = stack.getTagCompound();
			if (nbttagcompound == null) {
				return effectsList;
			}

			if (nbttagcompound.hasKey("E")) {
				int effectLength = nbttagcompound.getInteger("L");
				NBTTagList nbttaglist = nbttagcompound.getTagList("E", 10);
				IBeverageEffect[] effects = new IBeverageEffect[effectLength];
				for (int i = 0; i < nbttaglist.tagCount(); i++) {
					NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
					byte byte0 = nbttagcompound1.getByte("S");
					if (byte0 >= 0 && byte0 < effects.length) {
						effects[byte0] = BeverageManager.effectList[nbttagcompound1.getInteger("ID")];
					}
				}
				effectsList = Arrays.asList(effects);
			}

			return effectsList;
		}

		public static void saveEffects(ItemStack stack, List<IBeverageEffect> effects) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			NBTTagList nbttaglist = new NBTTagList();
			nbttagcompound.setInteger("L", effects.size());
			for (int i = 0; i < effects.size(); i++) {
				IBeverageEffect effect = effects.get(i);
				if (effect != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("S", (byte) i);
					nbttagcompound1.setInteger("ID", effect.getId());
					nbttaglist.appendTag(nbttagcompound1);
				}
			}
			nbttagcompound.setTag("E", nbttaglist);

			stack.setTagCompound(nbttagcompound);
		}

	}

	public final BeverageInfo[] beverages;

	public ItemBeverage(BeverageInfo... beverages) {
		super(1, 0.2f);
		setMaxStackSize(1);
		this.beverages = beverages;
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		List<IBeverageEffect> effects = BeverageInfo.loadEffects(itemstack);

		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(this, itemstack);
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

		if (!Proxies.common.isSimulating(world)) {
			return itemstack;
		}

		for (IBeverageEffect effect : effects) {
			effect.doEffect(world, entityplayer);
		}

		return itemstack;
	}

	@Override
	public float getSaturationModifier(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		BeverageInfo beverage = beverages[meta];
		return beverage.saturation;
	}
	
	@Override
	public int getHealAmount(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		BeverageInfo beverage = beverages[meta];
		return beverage.heal;
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
		BeverageInfo beverage = beverages[meta];

		if (entityplayer.canEat(beverage.isAlwaysEdible)) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < beverages.length; i++) {
			if (Config.isDebug || !beverages[i].isSecret) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		List<IBeverageEffect> effects = BeverageInfo.loadEffects(itemstack);

		for (IBeverageEffect effect : effects) {
			if (effect.getDescription() != null) {
				list.add(effect.getDescription());
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + beverages[stack.getItemDamage()].name;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for(int i = 0;i < beverages.length;i++){
			
			BeverageInfo info = beverages[i];
			manager.registerItemModel(item, i, info.name);
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || beverages[itemstack.getItemDamage()].secondaryColor == 0) {
			return beverages[itemstack.getItemDamage()].primaryColor;
		} else {
			return beverages[itemstack.getItemDamage()].secondaryColor;
		}
	}

}
