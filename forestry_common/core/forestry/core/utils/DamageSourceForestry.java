/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
