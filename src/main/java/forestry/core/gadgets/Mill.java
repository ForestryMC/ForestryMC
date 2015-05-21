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
package forestry.core.gadgets;

import forestry.core.network.PacketPayload;

public abstract class Mill extends TileBase {

	public int charge = 0;
	public float speed;
	public int stage = 0;
	public float progress;

	public Mill() {
		speed = 0.01F;
	}

	@Override
	public void updateClientSide() {
		update(false);
	}

	@Override
	public void updateServerSide() {
		update(true);
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(2, 1, 0);
		payload.intPayload[0] = charge;
		payload.intPayload[1] = stage;

		payload.floatPayload[0] = speed;
		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		charge = payload.intPayload[0];
		stage = payload.intPayload[1];

		speed = payload.floatPayload[0];
	}

	private void update(boolean isSimulating) {

		// Stop gracefully if discharged.
		if (charge <= 0) {
			if (stage > 0) {
				progress += speed;
			}
			if (progress > 0.5) {
				stage = 2;
			}
			if (progress > 1) {
				progress = 0;
				stage = 0;
			}
			return;
		}

		// Update blades
		progress += speed;
		if (stage <= 0) {
			stage = 1;
		}

		if (progress > 0.5 && stage == 1) {
			stage = 2;
			if (charge < 7 && isSimulating) {
				charge++;
				setNeedsNetworkUpdate();
			}
		}
		if (progress > 1) {
			progress = 0;
			stage = 0;

			// Fully charged! Do something!
			if (charge >= 7) {
				activate();
			}
		}

	}

	protected abstract void activate();
}
