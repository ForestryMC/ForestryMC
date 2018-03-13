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
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SoundUtil {
	@SideOnly(Side.CLIENT)
	public static void playButtonClick() {
		playSoundEvent(SoundEvents.UI_BUTTON_CLICK);
	}

	@SideOnly(Side.CLIENT)
	public static void playSoundEvent(SoundEvent soundIn) {
		playSoundEvent(soundIn, 1.0f);
	}

	@SideOnly(Side.CLIENT)
	public static void playSoundEvent(SoundEvent soundIn, float pitchIn) {
		Minecraft minecraft = Minecraft.getMinecraft();
		SoundHandler soundHandler = minecraft.getSoundHandler();
		PositionedSoundRecord sound = PositionedSoundRecord.getMasterRecord(soundIn, pitchIn);
		soundHandler.playSound(sound);
	}
}
