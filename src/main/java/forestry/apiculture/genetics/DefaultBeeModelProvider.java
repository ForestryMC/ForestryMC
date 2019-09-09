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

import javax.annotation.Nullable;
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

public class DefaultBeeModelProvider implements IBeeModelProvider {

	public static final DefaultBeeModelProvider instance = new DefaultBeeModelProvider();

	private DefaultBeeModelProvider() {

	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	private static EnumMap<EnumBeeType, ModelResourceLocation> models;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModels(Item item, IModelManager manager) {
		String beeIconDir = "bees/default/";
		EnumBeeType beeType = ((ItemBeeGE) item).getType();
		String beeTypeNameBase = beeIconDir + beeType.toString().toLowerCase(Locale.ENGLISH);

		if (models == null) {
			models = new EnumMap<>(EnumBeeType.class);
		}

		models.put(beeType, manager.getModelLocation(beeTypeNameBase));
		//TODO flatten or work out what this maps to
		//		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + beeTypeNameBase));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ModelResourceLocation getModel(EnumBeeType type) {
		return models.get(type);
	}
}
