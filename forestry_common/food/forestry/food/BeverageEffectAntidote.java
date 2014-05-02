/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

import forestry.core.proxy.Proxies;

public class BeverageEffectAntidote extends BeverageEffect {

	private float chance = 0.2f;

	public BeverageEffectAntidote(int id, float chance) {
		super(id);
		this.chance = chance;
		this.description = "beverage.effect.antidote";
	}

	@Override
	public void doEffect(World world, EntityPlayer player) {

		if (world.rand.nextFloat() >= chance)
			return;

		Proxies.common.removePotionEffect(player, Potion.poison);
	}

	@Override
	public String getLevel() {
		if (chance > 0.5f)
			return "III";
		else if (chance > 0.2f)
			return "II";
		else
			return "I";
	}

}
