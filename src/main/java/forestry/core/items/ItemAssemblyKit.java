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

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import forestry.core.proxy.Proxies;

public class ItemAssemblyKit extends ItemForestry {
	private final ItemStack assembled;
	private final boolean motionOnOpen;

	public ItemAssemblyKit(ItemStack assembled) {
		this(assembled, false);
	}

	public ItemAssemblyKit(ItemStack assembled, boolean motionOnOpen) {
		super();
		maxStackSize = 24;
		this.assembled = assembled;
		this.motionOnOpen = motionOnOpen;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			itemstack.stackSize--;
			EntityItem entity = new EntityItem(world, entityplayer.posX, entityplayer.posY, entityplayer.posZ, assembled.copy());

			if (motionOnOpen) {
				float f1 = 0.3F;
				entity.motionX = -MathHelper.sin((entityplayer.rotationYaw / 180F) * 3.141593F)
						* MathHelper.cos((entityplayer.rotationPitch / 180F) * 3.141593F) * f1;
				entity.motionZ = MathHelper.cos((entityplayer.rotationYaw / 180F) * 3.141593F)
						* MathHelper.cos((entityplayer.rotationPitch / 180F) * 3.141593F) * f1;
				entity.motionY = -MathHelper.sin((entityplayer.rotationPitch / 180F) * 3.141593F) * f1 + 0.1F;
				f1 = 0.02F;
				float f3 = world.rand.nextFloat() * 3.141593F * 2.0F;
				f1 *= world.rand.nextFloat();
				entity.motionX += Math.cos(f3) * f1;
				entity.motionY += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F;
				entity.motionZ += Math.sin(f3) * f1;
			}

			world.spawnEntityInWorld(entity);
		}
		return itemstack;
	}

}
