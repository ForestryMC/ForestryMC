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
package forestry.greenhouse.multiblock.blocks.client;

import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.greenhouse.IGreenhouseBlockHandler;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.multiblock.blocks.GreenhouseBlock;

/**
 * Only a small logic block for the client. Because he has to know which blocks are inside a greenhouse and which not.
 */
@SideOnly(Side.CLIENT)
public class ClientBlock extends GreenhouseBlock<ClientBlock> {

	public ClientBlock(IGreenhouseProvider manager, BlockPos pos) {
		super(manager, pos);
	}

	@Override
	public IGreenhouseBlockHandler getHandler() {
		return ClientBlockHandler.getInstance();
	}
}
