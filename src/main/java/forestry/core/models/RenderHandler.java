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
package forestry.core.models;

import java.util.ArrayList;

import net.minecraft.util.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;

public class RenderHandler {

	public static final ArrayList<BlockModelIndex> blockModels = new ArrayList<BlockModelIndex>();
	public static final ArrayList<ModelIndex> models = new ArrayList<ModelIndex>();

	public static void registerModels(ModelBakeEvent event) {
		IRegistry registry = event.modelRegistry;
		for (final BlockModelIndex index : blockModels) {
			Object o = registry.getObject(index.blockModelLocation);
			registry.putObject(index.blockModelLocation, index.model);
			registry.putObject(index.itemModelLocation, index.model);
		}
		
		for (final ModelIndex index : models) {
			registry.putObject(index.modelLocation, index.model);
		}
	}

}
