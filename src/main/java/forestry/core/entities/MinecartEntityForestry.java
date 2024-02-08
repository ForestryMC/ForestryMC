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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import net.minecraftforge.network.NetworkHooks;

import forestry.core.tiles.ITitled;
import forestry.core.utils.PlayerUtil;

//TODO - check nothing missing from MinecartEntity now that this extends AbstractMinecartEntity
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;

public abstract class MinecartEntityForestry extends AbstractMinecart implements ITitled {

	public MinecartEntityForestry(EntityType<? extends MinecartEntityForestry> type, Level world) {
		super(type, world);
		setCustomDisplay(true);
	}

	public MinecartEntityForestry(EntityType<?> type, Level world, double posX, double posY, double posZ) {
		super(type, world, posX, posY, posZ);
		setCustomDisplay(true);
	}

	// Needed to spawn the entity on the client
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		InteractionResult ret = super.interact(player, hand);
		if (ret.consumesAction()) {
			return ret;
		}
		PlayerUtil.actOnServer(player, this::openGui);
		return InteractionResult.sidedSuccess(this.level.isClientSide);
	}

	protected abstract void openGui(ServerPlayer player);

	/* MinecartEntity */
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
	public abstract BlockState getDisplayBlockState();

	// cart itemStack
	@Override
	public abstract ItemStack getPickResult();

	@Override
	public void destroy(DamageSource damageSource) {
		super.destroy(damageSource);
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			Block block = getDisplayBlockState().getBlock();
			spawnAtLocation(new ItemStack(block), 0.0F);
		}
	}

	@Override
	public Component getName() {
		return Component.translatable(getUnlocalizedTitle());
	}

	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		ItemStack cartItem = getPickResult();
		return cartItem.getDescriptionId();
	}

	@Override
	public Type getMinecartType() {
		return null;
	}
}
