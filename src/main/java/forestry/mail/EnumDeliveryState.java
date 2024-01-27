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

import net.minecraft.network.chat.Component;

import forestry.api.mail.IPostalState;

public enum EnumDeliveryState implements IPostalState {
	OK("for.chat.mail.ok"),
	NO_MAILBOX("for.chat.mail.no.mailbox"),
	NOT_MAILABLE("for.chat.mail.not.mailable"),
	ALREADY_MAILED("for.chat.mail.already.mailed"),
	NOT_POSTPAID("for.chat.mail.not.postpaid"),
	MAILBOX_FULL("for.chat.mail.mailbox.full");

	private final String unlocalizedDescription;

	EnumDeliveryState(String unlocalizedDescription) {
		this.unlocalizedDescription = unlocalizedDescription;
	}

	@Override
	public boolean isOk() {
		return this == OK;
	}

	@Override
	public Component getDescription() {
		return Component.translatable(unlocalizedDescription);
	}
}
