/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public interface ILetterHandler {
	IPostalState handleLetter(ServerWorld world, IMailAddress recipient, ItemStack letterStack, boolean doLodge);
}
