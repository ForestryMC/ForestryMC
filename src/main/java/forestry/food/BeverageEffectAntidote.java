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
package forestry.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class BeverageEffectAntidote extends BeverageEffect {

	private float chance = 0.2f;

	public BeverageEffectAntidote(int id, float chance) {
		super(id);
		this.chance = chance;
		this.description = "beverage.effect.antidote";
	}

	@Override
	public void doEffect(World world, EntityPlayer player) {

		if (world.rand.nextFloat() >= chance) {
			return;
		}

		player.removePotionEffect(Potion.poison.getId());
	}

	@Override
	public String getLevel() {
		if (chance > 0.5f) {
			return "III";
		} else if (chance > 0.2f) {
			return "II";
		} else {
			return "I";
		}
	}

}
