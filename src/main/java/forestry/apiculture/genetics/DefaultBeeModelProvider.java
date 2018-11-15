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

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.core.IModelManager;
import forestry.apiculture.items.ItemBeeGE;

public class DefaultBeeModelProvider implements IBeeModelProvider {

	public static final DefaultBeeModelProvider instance = new DefaultBeeModelProvider();

	private DefaultBeeModelProvider() {

	}

	@SideOnly(Side.CLIENT)
	@Nullable
	private static EnumMap<EnumBeeType, ModelResourceLocation> models;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(Item item, IModelManager manager) {
		String beeIconDir = "bees/default/";
		EnumBeeType beeType = ((ItemBeeGE) item).getType();
		String beeTypeNameBase = beeIconDir + beeType.toString().toLowerCase(Locale.ENGLISH);

		if (models == null) {
			models = new EnumMap<>(EnumBeeType.class);
		}

		models.put(beeType, manager.getModelLocation(beeTypeNameBase));
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:" + beeTypeNameBase));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(EnumBeeType type) {
		return models.get(type);
	}
}
