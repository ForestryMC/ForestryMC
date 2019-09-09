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

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DamageSourceForestry extends DamageSource {

	public DamageSourceForestry(String ident) {
		super(ident);
		setDamageBypassesArmor();
	}

	@Override
	public ITextComponent getDeathMessage(LivingEntity living) {
		LivingEntity other = living.getAttackingEntity();
		String ssp = "death." + this.damageType;
		String smp = ssp + ".player";

		if (other != null) {
			return new TranslationTextComponent(smp, living.getDisplayName(), other.getDisplayName());
		} else {
			return new TranslationTextComponent(ssp, living.getDisplayName());
		}
	}

}
