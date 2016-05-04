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
package forestry.lepidopterology.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.core.models.ModelIndex;
import forestry.core.models.ModelManager;
import forestry.core.proxy.Proxies;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.render.ModelButterflyItem;
import forestry.lepidopterology.render.RenderButterflyEntity;

public class ProxyLepidopterologyClient extends ProxyLepidopterology {

	@Override
	public void preInitializeRendering() {
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly.class, new RenderButterflyEntity.Factory());
		Proxies.render.registerModel(new ModelIndex(ModelManager.getInstance().getModelLocation("butterflyGE"), new ModelButterflyItem()));

		Minecraft minecraft = Minecraft.getMinecraft();

		ItemColors itemColors = minecraft.getItemColors();

		itemColors.registerItemColorHandler(new ButterflyItemColor(),
				PluginLepidopterology.items.butterflyGE
		);
	}

	private static class ButterflyItemColor implements IItemColor {
		@Override
		public int getColorFromItemstack(ItemStack stack, int tintIndex) {
			if (stack.hasTagCompound()) {
				IAlleleSpecies species = AlleleManager.alleleRegistry.getIndividual(stack).getGenome().getPrimary();
				if (species != null) {
					return species.getSpriteColour(tintIndex);
				}
			}
			return 0xffffff;
		}
	}
}
