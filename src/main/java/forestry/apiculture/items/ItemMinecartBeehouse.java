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
package forestry.apiculture.items;

import java.util.List;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeeHousingBase;
import forestry.apiculture.entities.EntityMinecartBeehouse;
import forestry.core.render.TextureManager;

public class ItemMinecartBeehouse extends ItemMinecart {
	private final String[] definition = new String[]{"cart.beehouse", "cart.apiary"};

	public ItemMinecartBeehouse() {
		super(0);
		setMaxDamage(0);
		setHasSubtypes(true);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, null);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float facingX, float facingY, float facingZ) {
		if (!BlockRailBase.func_150051_a(world.getBlock(x, y, z))) {
			return false;
		}

		if (!world.isRemote) {
			EntityMinecartBeeHousingBase entityMinecart;
			if (itemStack.getItemDamage() == 0) {
				entityMinecart = new EntityMinecartBeehouse(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F));
			} else {
				entityMinecart = new EntityMinecartApiary(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F));
			}
			entityMinecart.setOwner(player.getGameProfile());

			if (itemStack.hasDisplayName()) {
				entityMinecart.setMinecartName(itemStack.getDisplayName());
			}

			world.spawnEntityInWorld(entityMinecart);
		}

		--itemStack.stackSize;
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() >= definition.length || stack.getItemDamage() < 0) {
			return "item.forestry.unknown";
		} else {
			return "item.for." + definition[stack.getItemDamage()];
		}
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[definition.length];
		for (int i = 0; i < definition.length; i++) {
			icons[i] = TextureManager.registerTex(register, definition[i]);
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage >= definition.length || damage < 0) {
			return icons[0];
		} else {
			return icons[damage];
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < definition.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}
}
