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
package forestry.lepidopterology.render;

import org.lwjgl.opengl.GL11;

import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.proxy.Proxies;
import forestry.lepidopterology.blocks.BlockCocoon;
import forestry.lepidopterology.genetics.MothDefinition;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class RendererCocoon extends TileEntitySpecialRenderer<TileCocoon> {
	
	private static ModelCocoon modelCocoon = new ModelCocoon();
	
	public RendererCocoon() {
	}
	
	@Override
	public void renderTileEntityAt(TileCocoon te, double x, double y, double z, float partialTicks, int destroyStage) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		
		if(state == null || !(state.getBlock() instanceof BlockCocoon)){
			return;
		}
		IButterfly butterfly = te.getCaterpillar();
		if(butterfly == null){
			butterfly = MothDefinition.BombyxMori.getIndividual();
		}
		render(butterfly, te.getAge(), x, y, z);
	}
	
	private static void render(IButterfly butterfly, int age, double x, double y, double z){
		IAlleleButterflySpecies species = butterfly.getGenome().getPrimary();
		GL11.glPushMatrix();
		GL11.glTranslated((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotated(180, 0F, 0F, 1F);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		Proxies.render.bindTexture(new ResourceLocation(species.getModID(), species.getCocoonProvider().getCocoonTexture(age)));
		modelCocoon.renderCucoon(age);
		GL11.glPopMatrix();
	}

}
