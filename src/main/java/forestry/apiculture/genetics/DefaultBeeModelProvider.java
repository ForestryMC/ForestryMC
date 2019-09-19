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
package forestry.apiculture.genetics;

import java.util.EnumMap;
import java.util.Locale;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.core.IModelManager;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.config.Constants;

public class DefaultBeeModelProvider implements IBeeModelProvider {

	@OnlyIn(Dist.CLIENT)
	private static final EnumMap<EnumBeeType, ModelResourceLocation> models = new EnumMap<>(EnumBeeType.class);
	public static final String MODEL_DIR = "bees/default/";

	public static final DefaultBeeModelProvider instance = new DefaultBeeModelProvider();

	private DefaultBeeModelProvider() {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModels(Item item, IModelManager manager) {
		EnumBeeType beeType = ((ItemBeeGE) item).getType();
		String modelLocation = MODEL_DIR + beeType.toString().toLowerCase(Locale.ENGLISH);

		models.put(beeType, new ModelResourceLocation(Constants.MOD_ID + ":" + modelLocation, "inventory"));
		//TODO flatten or work out what this maps to
		//		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + beeTypeNameBase));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ModelResourceLocation getModel(EnumBeeType type) {
		return models.get(type);
	}
}
