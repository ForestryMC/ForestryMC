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

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.apiculture.entities.EntityMinecartBeehouse;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemMinecartBeehouse extends ItemMinecart {

	public ItemMinecartBeehouse() {
		super(0);
		maxStackSize = 1;
		setMaxDamage(0);
		setHasSubtypes(false);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, null);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		itemIcon = TextureManager.registerTex(register, StringUtil.cleanItemName(this));
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float facingX, float facingY, float facingZ) {
		if (!BlockRailBase.func_150051_a(world.getBlock(x, y, z))) {
			return false;
		}

		if (!world.isRemote) {
			EntityMinecartBeehouse entityMinecart = new EntityMinecartBeehouse(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F));
			entityMinecart.setOwner(player.getGameProfile());

			if (itemStack.hasDisplayName()) {
				entityMinecart.setMinecartName(itemStack.getDisplayName());
			}

			world.spawnEntityInWorld(entityMinecart);
		}

		--itemStack.stackSize;
		return true;
	}
}
