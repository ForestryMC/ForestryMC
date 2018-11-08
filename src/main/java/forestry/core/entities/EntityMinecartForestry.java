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
package forestry.core.entities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import forestry.core.gui.GuiHandler;
import forestry.core.gui.IGuiHandlerEntity;
import forestry.core.tiles.ITitled;
import forestry.core.utils.Translator;

public abstract class EntityMinecartForestry extends EntityMinecart implements ITitled, IGuiHandlerEntity {

	@SuppressWarnings("unused")
	public EntityMinecartForestry(World world) {
		super(world);
		setHasDisplayTile(true);
	}

	public EntityMinecartForestry(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		setHasDisplayTile(true);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand))) {
			return true;
		}

		if (!world.isRemote) {
			GuiHandler.openGui(player, this);
		}
		return true;
	}

	@Override
	public Type getType() {
		return Type.CHEST;
	}

	/* EntityMinecart */
	@Override
	public boolean canBeRidden() {
		return false;
	}

	@Override
	public boolean isPoweredCart() {
		return false;
	}

	// cart contents
	@Override
	public abstract IBlockState getDisplayTile();

	// cart itemStack
	@Override
	public abstract ItemStack getCartItem();

	@Override
	public void killMinecart(DamageSource damageSource) {
		super.killMinecart(damageSource);
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			Block block = getDisplayTile().getBlock();
			entityDropItem(new ItemStack(block), 0.0F);
		}
	}

	// fix cart contents rendering as black in the End dimension
	@Override
	public float getBrightness() {
		return 1.0f;
	}

	@Override
	public String getName() {
		return Translator.translateToLocal(getUnlocalizedTitle());
	}

	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		ItemStack cartItem = getCartItem();
		return cartItem.getTranslationKey() + ".name";
	}

	@Override
	public int getIdOfEntity() {
		return getEntityId();
	}
}
