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

import forestry.api.recipes.IGenericCrate;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrated extends Item implements IGenericCrate {

	private final ItemStack contained;
	private final boolean usesOreDict;

	public ItemCrated(ItemStack contained, boolean usesOreDict) {
		this.contained = contained;
		this.usesOreDict = usesOreDict;
	}

	public boolean usesOreDict() {
		return usesOreDict;
	}

	@Override
	public void setContained(ItemStack crated, ItemStack contained) {
	}

	@Override
	public ItemStack getContained(ItemStack crate) {
		return contained;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(world)) {
			if (contained == null || itemstack.stackSize == 0) {
				return itemstack;
			}

			itemstack.stackSize--;
			EntityItem entity = new EntityItem(world, entityplayer.posX, entityplayer.posY, entityplayer.posZ, new ItemStack(contained.getItem(), 9,
					contained.getItemDamage()));
			entity.setPickupDelay(40);
			
			float f1 = 0.3F;
			entity.motionX = -MathHelper.sin((entityplayer.rotationYaw / 180F) * 3.141593F) * MathHelper.cos((entityplayer.rotationPitch / 180F) * 3.141593F)
					* f1;
			entity.motionZ = MathHelper.cos((entityplayer.rotationYaw / 180F) * 3.141593F) * MathHelper.cos((entityplayer.rotationPitch / 180F) * 3.141593F)
					* f1;
			entity.motionY = -MathHelper.sin((entityplayer.rotationPitch / 180F) * 3.141593F) * f1 + 0.1F;
			f1 = 0.02F;
			float f3 = world.rand.nextFloat() * 3.141593F * 2.0F;
			f1 *= world.rand.nextFloat();
			entity.motionX += Math.cos(f3) * f1;
			entity.motionY += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F;
			entity.motionZ += Math.sin(f3) * f1;

			world.spawnEntityInWorld(entity);
		}
		return itemstack;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (contained == null) {
			return StatCollector.translateToLocal("item.for.crate.name");
		} else {
			return StringUtil.localize("item.crated.adj") + " " + Proxies.common.getDisplayName(contained);
		}
	}

}
