/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class RainSubstrate {
	/**
	 * Rain substrate capable of activating the rainmaker.
	 */
	public ItemStack item;
	/**
	 * Duration of the rain shower triggered by this substrate in Minecraft ticks.
	 */
	public int duration;
	/**
	 * Speed of activation sequence triggered.
	 */
	public float speed;

	public boolean reverse;

	public RainSubstrate(ItemStack item, int duration, float speed) {
		this(item, duration, speed, false);
	}

	public RainSubstrate(ItemStack item, float speed) {
		this(item, 0, speed, true);
	}

	public RainSubstrate(ItemStack item, int duration, float speed, boolean reverse) {
		this.item = item;
		this.duration = duration;
		this.speed = speed;
		this.reverse = reverse;
	}
}
