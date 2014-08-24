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
package forestry.core.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;


import forestry.core.CreativeTabForestry;

public class ItemForestryFood extends ItemFood {

	private boolean isAlwaysEdible = false;
	private boolean isDrink = false;

	private int healAmount = 0;
	private final float saturationModifier;

	private int potionId;
	private int potionDuration;
	private int potionAmplifier;
	private float potionEffectProbability;

	public ItemForestryFood(int heal) {
		this(heal, 0.6f);
	}

	public ItemForestryFood(int heal, float saturation) {
		super(0, 0, false);
		healAmount = heal;
		saturationModifier = saturation;
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public int func_150905_g(ItemStack p_150905_1_) {
		return healAmount;
	}

	@Override
	public float func_150906_h(ItemStack p_150906_1_) {
		return saturationModifier;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(healAmount, saturationModifier);
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		if (!world.isRemote && potionId > 0 && world.rand.nextFloat() < potionEffectProbability)
			entityplayer.addPotionEffect(new PotionEffect(potionId, potionDuration * 20, potionAmplifier));

		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		if (isDrink)
			return EnumAction.drink;
		else
			return EnumAction.eat;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (entityplayer.canEat(isAlwaysEdible))
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	public ItemForestryFood setIsDrink() {
		isDrink = true;
		return this;
	}

	public ItemForestryFood setPotionEffect(int i, int j, int k, float f) {
		potionId = i;
		potionDuration = j;
		potionAmplifier = k;
		potionEffectProbability = f;
		return this;
	}

	public ItemForestryFood setAlwaysEdible() {
		isAlwaysEdible = true;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		itemIcon = TextureManager.getInstance().registerTex(register, StringUtil.cleanItemName(this));
	}

}
