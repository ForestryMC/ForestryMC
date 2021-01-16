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
package forestry.mail;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.EnumTradeStationState;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.ITradeStationInfo;

public class TradeStationInfo implements ITradeStationInfo {
	private final IMailAddress address;
	private final GameProfile owner;
	private final ItemStack tradegood;
	private final NonNullList<ItemStack> required;
	private final EnumTradeStationState state;

	public TradeStationInfo(IMailAddress address, GameProfile owner, ItemStack tradegood, NonNullList<ItemStack> required, EnumTradeStationState state) {
		if (address.getType() != EnumAddressee.TRADER) {
			throw new IllegalArgumentException("TradeStation address must be a trader");
		}
		this.address = address;
		this.owner = owner;
		this.tradegood = tradegood;
		this.required = required;
		this.state = state;
	}

	@Override
	public IMailAddress getAddress() {
		return address;
	}

	@Override
	public GameProfile getOwner() {
		return owner;
	}

	@Override
	public ItemStack getTradegood() {
		return tradegood;
	}

	@Override
	public NonNullList<ItemStack> getRequired() {
		return required;
	}

	@Override
	public EnumTradeStationState getState() {
		return state;
	}
}
