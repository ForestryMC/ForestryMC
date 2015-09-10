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
package forestry.core.render;

import java.util.ArrayList;

import net.minecraft.util.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;

public class BlockRenderingHandler {

	public static final ArrayList<BlockModelIndex> byBlockModelRenderer = new ArrayList<BlockModelIndex>();
	public static final ArrayList<ItemModelIndex> byItemModelRenderer = new ArrayList<ItemModelIndex>();

	public static void checkModels(ModelBakeEvent event){
		IRegistry registry = event.modelRegistry;
		for(final BlockModelIndex index : byBlockModelRenderer)
		{
		    registry.putObject(index.blockModelLocation, index.model);
		    registry.putObject(index.itemModelLocation, index.model);
		}
		
		for(final ItemModelIndex index : byItemModelRenderer)
		{
		    registry.putObject(index.modelLocation, index.model);
		}
	}

}
