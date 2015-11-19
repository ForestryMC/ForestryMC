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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.StringUtil;

public class ItemCrated extends ItemForestry {

	private final ItemStack contained;
	private final boolean usesOreDict;

	public ItemCrated(ItemStack contained, boolean usesOreDict) {
		this.contained = contained;
		this.usesOreDict = usesOreDict;
	}

	public boolean usesOreDict() {
		return usesOreDict;
	}

	public ItemStack getContained() {
		return contained;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			if (contained == null || itemstack.stackSize == 0) {
				return itemstack;
			}

			itemstack.stackSize--;

			ItemStack dropStack = contained.copy();
			dropStack.stackSize = 9;
			ItemStackUtil.dropItemStackAsEntity(dropStack, world, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 40);
		}
		return itemstack;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (contained == null) {
			return StatCollector.translateToLocal("item.for.crate.name");
		} else {
			String containedName = Proxies.common.getDisplayName(contained);
			return StringUtil.localizeAndFormat("item.crated.grammar", containedName);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		String textureName = (contained == null) ? "crate" : "crate-filled";
		itemIcon = TextureManager.registerTex(register, textureName);
	}

}
