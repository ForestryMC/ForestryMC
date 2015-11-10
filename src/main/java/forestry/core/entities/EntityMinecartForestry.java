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
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import forestry.api.core.ForestryAPI;
import forestry.core.access.AccessHandler;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.gui.IHintSource;
import forestry.core.network.GuiId;
import forestry.core.tiles.ITitled;

public abstract class EntityMinecartForestry extends EntityMinecart implements ITitled, IRestrictedAccess, IHintSource {
	private final AccessHandler accessHandler = new AccessHandler(this);

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
	public final boolean interactFirst(EntityPlayer player) {
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player))) {
			return true;
		}

		if (!worldObj.isRemote) {
			player.openGui(ForestryAPI.instance, getGuiId().ordinal(), worldObj, getEntityId(), -1, 0);
		}
		return true;
	}

	protected abstract GuiId getGuiId();

	public void setOwner(GameProfile owner) {
		accessHandler.setOwner(owner);
	}

	@Override
	public int getMinecartType() {
		return -1;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		accessHandler.readFromNBT(nbtTagCompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		accessHandler.writeToNBT(nbtTagCompound);
	}

	/* EntityMinecart */
	@Override
	public boolean canBeRidden() {
		return false;
	}

	// cart contents
	@Override
	public abstract Block func_145820_n();

	// cart contents meta
	@Override
	public abstract int getDisplayTileData();

	// cart itemStack
	@Override
	public abstract ItemStack getCartItem();

	@Override
	public void killMinecart(DamageSource damageSource) {
		super.killMinecart(damageSource);
		entityDropItem(new ItemStack(func_145820_n(), 1, getDisplayTileData()), 0.0F);
	}

	// fix cart contents rendering as black in the End dimension
	@Override
	public float getBrightness(float p_70013_1_) {
		return 1.0f;
	}

	@Override
	public String getCommandSenderName() {
		return StatCollector.translateToLocal(getUnlocalizedTitle());
	}

	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		ItemStack cartItem = getCartItem();
		return cartItem.getUnlocalizedName() + ".name";
	}

	/* IRestrictedAccess */
	@Override
	public IAccessHandler getAccessHandler() {
		return accessHandler;
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {

	}
}
