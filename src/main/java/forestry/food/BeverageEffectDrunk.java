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
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BeverageEffectDrunk extends BeverageEffect {

	private final PotionEffect drunkEffect;
	private final float chance;

	public BeverageEffectDrunk(int id, float chance) {
		super(id);
		this.chance = chance;
		this.description = "beverage.effect.alcoholic";
		this.drunkEffect = new PotionEffect(Potion.confusion.id, 25 * 20, 0);
	}

	@Override
	public void doEffect(World world, EntityPlayer player) {
		if (world.rand.nextFloat() < chance) {
			player.addPotionEffect(drunkEffect);
		}
	}

}
