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

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public class SoundUtil {
	@OnlyIn(Dist.CLIENT)
	public static void playButtonClick() {
		playSoundEvent(SoundEvents.UI_BUTTON_CLICK);
	}

	@OnlyIn(Dist.CLIENT)
	public static void playSoundEvent(SoundEvent soundIn) {
		playSoundEvent(soundIn, 1.0f);
	}

	@OnlyIn(Dist.CLIENT)
	public static void playSoundEvent(SoundEvent soundIn, float pitchIn) {
		Minecraft minecraft = Minecraft.getInstance();
		SoundHandler soundHandler = minecraft.getSoundHandler();
		SimpleSound sound = SimpleSound.master(soundIn, pitchIn);
		soundHandler.play(sound);
	}
}
