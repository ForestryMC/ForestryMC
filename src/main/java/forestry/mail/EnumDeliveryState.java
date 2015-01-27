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

import java.util.Locale;

import forestry.api.mail.IPostalState;

public enum EnumDeliveryState implements IPostalState {
	OK, NO_MAILBOX, NOT_MAILABLE, ALREADY_MAILED, NOT_POSTPAID, MAILBOX_FULL, RESPONSE_NOT_MAILABLE;

	@Override
	public boolean isOk() {
		return this == OK;
	}

	@Override
	public String getIdentifier() {
		return this.toString().toLowerCase(Locale.ENGLISH);
	}
}
