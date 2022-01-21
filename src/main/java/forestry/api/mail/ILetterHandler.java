/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;

public interface ILetterHandler {
	IPostalState handleLetter(ServerLevel world, IMailAddress recipient, ItemStack letterStack, boolean doLodge);
}
