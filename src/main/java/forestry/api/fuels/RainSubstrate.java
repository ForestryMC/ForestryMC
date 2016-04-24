/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class RainSubstrate {
	private final ItemStack item;
	private final int duration;
	private final float speed;
	private final boolean reverse;

	public RainSubstrate(ItemStack item, float speed) {
		this(item, 0, speed, true);
	}

	public RainSubstrate(ItemStack item, int duration, float speed) {
		this(item, duration, speed, false);
	}

	public RainSubstrate(ItemStack item, int duration, float speed, boolean reverse) {
		this.item = item;
		this.duration = duration;
		this.speed = speed;
		this.reverse = reverse;
	}

	/**
	 * Rain substrate capable of activating the rainmaker.
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Duration of the rain shower triggered by this substrate in Minecraft ticks.
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Speed of activation sequence triggered.
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * @return true if the substrate stops rain instead of creating rain
	 */
	public boolean isReverse() {
		return reverse;
	}
}
