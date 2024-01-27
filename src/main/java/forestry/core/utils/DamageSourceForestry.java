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
package forestry.core.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;

public class DamageSourceForestry extends DamageSource {

	public DamageSourceForestry(String ident) {
		super(ident);
		bypassArmor();
	}

	@Override
	public Component getLocalizedDeathMessage(LivingEntity living) {
		LivingEntity other = living.getKillCredit();
		String ssp = "death." + this.msgId;
		String smp = ssp + ".player";

		if (other != null) {
			return Component.translatable(smp, living.getDisplayName(), other.getDisplayName());
		} else {
			return Component.translatable(ssp, living.getDisplayName());
		}
	}

}
