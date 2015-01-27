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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class DamageSourceForestry extends DamageSource {

	public DamageSourceForestry(String ident) {
		super(ident);
		setDamageBypassesArmor();
	}

	@Override
	public IChatComponent func_151519_b(EntityLivingBase living) {
		EntityLivingBase other = living.func_94060_bK();
		String ssp = "death." + this.damageType;
		String smp = ssp + ".player";

		if (other != null) {
			return new ChatComponentTranslation(smp, living.func_145748_c_(), other.func_145748_c_());
		} else {
			return new ChatComponentTranslation(ssp, living.func_145748_c_());
		}
	}

}
