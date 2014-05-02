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
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BeverageEffectDrunk extends BeverageEffect {

	private PotionEffect drunkEffect;
	private float chance = 0.2f;

	public BeverageEffectDrunk(int id, float chance) {
		super(id);
		this.chance = chance;
		this.description = "beverage.effect.alcoholic";
		drunkEffect = new PotionEffect(Potion.confusion.id, 25 * 20, 0);
	}

	@Override
	public void doEffect(World world, EntityPlayer player) {
		if (world.rand.nextFloat() < chance)
			player.addPotionEffect(drunkEffect);
	}

}
