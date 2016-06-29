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
package forestry.arboriculture.render;

import javax.annotation.Nonnull;

import static forestry.arboriculture.multiblock.EnumPilePosition.*;

import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.blocks.BlockPile;
import forestry.arboriculture.multiblock.EnumPilePosition;
import forestry.arboriculture.multiblock.ICharcoalPileControllerInternal;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.multiblock.MultiblockLogic;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class CharcoalPileRenderer extends TileEntitySpecialRenderer<TilePile> {
	
	public CharcoalPileRenderer() {
	}
	
	@Override
	public void renderTileEntityAt(@Nonnull TilePile pile, double x, double y, double z, float p_147500_8_, int destroyStage) {
		IBlockState state = pile.getWorld().getBlockState(pile.getPos());
		if (!(state.getBlock() instanceof BlockPile)) {
			return;
		}

		EnumPilePosition pilePosition = state.getValue(BlockPile.PILE_POSITION);
		MultiblockLogic<ICharcoalPileControllerInternal> logic = pile.getMultiblockLogic();
		if (pilePosition != EnumPilePosition.INTERIOR && logic.isConnected() && logic.getController() != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			Proxies.common.getClientInstance().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			
			boolean withWood = pile.getPileType() != EnumPileType.ASH;
			boolean isOnTop = logic.getController().getMaximumCoord().getY() == pile.getPos().getY();
			// TODO Add brightness to the textures.
			int brightness = state.getPackedLightmapCoords(pile.getWorld(), pile.getPos());
			IAlleleTreeSpecies treeSpecies = pile.getNextWoodPile();
			if (treeSpecies == null) {
				treeSpecies = (IAlleleTreeSpecies) TreeManager.treeRoot.getDefaultTemplate()[TreeManager.treeRoot.getSpeciesChromosomeType().ordinal()];
			}
			TextureAtlasSprite dirtSprite;
			if(pile.getPileType() == EnumPileType.ASH){
				dirtSprite = TextureManager.registerSprite(new ResourceLocation("forestry:blocks/ash"));
			}else{
				dirtSprite = TextureManager.registerSprite(new ResourceLocation("forestry:blocks/loam"));
			}
			TextureAtlasSprite woodSprite = treeSpecies.getWoodProvider().getSprite(false);
			TextureAtlasSprite woodTopSprite = treeSpecies.getWoodProvider().getSprite(true);
			if (pilePosition == BACK) {
				renderPileSide(withWood, dirtSprite, woodSprite, woodTopSprite, brightness);
			} else if (pilePosition == FRONT) {
				GlStateManager.rotate(180, 0F, 0F, 1F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				
				renderPileSide(withWood, dirtSprite, woodSprite, woodTopSprite, brightness);
			} else if (pilePosition == SIDE_LEFT) {
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				
				renderPileSide(withWood, dirtSprite, woodSprite, woodTopSprite, brightness);
			} else if (pilePosition == SIDE_RIGHT) {
				GlStateManager.rotate(180, 0F, 1F, 0F);
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				
				renderPileSide(withWood, dirtSprite, woodSprite, woodTopSprite, brightness);
			} else if (pilePosition == CORNER_BACK_RIGHT) {
				GlStateManager.rotate(180, 0F, 1F, 0F);
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				
				renderPileCorner(withWood, dirtSprite, woodSprite, woodTopSprite, brightness, isOnTop);
			} else if (pilePosition == CORNER_BACK_LEFT) {
				GlStateManager.rotate(90, 0F, 1F, 0F);
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				
				renderPileCorner(withWood, dirtSprite, woodSprite, woodTopSprite, brightness, isOnTop);
			} else if (pilePosition == CORNER_FRONT_LEFT) {
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				
				renderPileCorner(withWood, dirtSprite, woodSprite, woodTopSprite, brightness, isOnTop);
			} else if (pilePosition == CORNER_FRONT_RIGHT) {
				GlStateManager.rotate(270, 0F, 1F, 0F);
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				
				renderPileCorner(withWood, dirtSprite, woodSprite, woodTopSprite, brightness, isOnTop);
			}
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
	}

	public static void renderPileSide(boolean withWood, TextureAtlasSprite dirtSprite, TextureAtlasSprite woodSprite, TextureAtlasSprite woodTopSprite, int brightness) {
		Tessellator t = Tessellator.getInstance();
		VertexBuffer buffer = t.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, -0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		
		buffer.pos(0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		
		if(withWood){
			buffer.pos(0.1, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(0.1, -0.5, -0.5).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.1, -0.5, -0.7).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.1, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(0.1, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(0.1, -0.5, -0.7).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.1, -0.5, -0.7).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.1, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(-0.1, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.1, -0.5, -0.7).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.1, -0.5, -0.5).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.1, 0.3, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(-0.1, -0.5, -0.7).tex(woodTopSprite.getMinU(), woodTopSprite.getMinV()).endVertex();
			buffer.pos(0.1, -0.5, -0.7).tex(woodTopSprite.getMaxU(), woodTopSprite.getMinV()).endVertex();
			buffer.pos(0.1, -0.5, -0.5).tex(woodTopSprite.getMaxU(), woodTopSprite.getMaxV()).endVertex();
			buffer.pos(-0.1, -0.5, -0.5).tex(woodTopSprite.getMinU(), woodTopSprite.getMaxV()).endVertex();
			
			buffer.pos(-0.5, 0.3, 0.5).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.5, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(0.5, 0.3, 0.5).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(-0.5, 0.5, 0.5).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.5, 0.5).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.5, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(0.5, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.5, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.5, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
		}
		t.draw();
	}

	public static void renderPileCorner(boolean withWood, TextureAtlasSprite dirtSprite, TextureAtlasSprite woodSprite, TextureAtlasSprite woodTopSprite, int brightness, boolean isTop) {
		Tessellator t = Tessellator.getInstance();
		VertexBuffer buffer = t.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, -0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, -0.5, -0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		
		buffer.pos(-0.5, 0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(-0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMinV()).endVertex();
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMinU(), dirtSprite.getMaxV()).endVertex();
		buffer.pos(0.5, -0.5, 0.5).tex(dirtSprite.getMaxU(), dirtSprite.getMaxV()).endVertex();
		
		if (isTop && withWood) {
			
			buffer.pos(-0.3, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.3, -0.5, -0.5).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.3, -0.5, -0.7).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.3, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(-0.3, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.3, -0.5, -0.7).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.5, -0.5, -0.7).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.5, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			
			buffer.pos(-0.5, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(-0.5, -0.5, -0.7).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.5, -0.5, -0.5).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(-0.5, 0.3, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(-0.5, -0.5, -0.7).tex(woodTopSprite.getMinU(), woodTopSprite.getMinV()).endVertex();
			buffer.pos(-0.3, -0.5, -0.7).tex(woodTopSprite.getMaxU(), woodTopSprite.getMinV()).endVertex();
			buffer.pos(-0.3, -0.5, -0.5).tex(woodTopSprite.getMaxU(), woodTopSprite.getMaxV()).endVertex();
			buffer.pos(-0.5, -0.5, -0.5).tex(woodTopSprite.getMinU(), woodTopSprite.getMaxV()).endVertex();
		}
        t.draw();
		
		GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
		if(withWood){
	        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			if (isTop) {
				
				buffer.pos(0.5, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
				buffer.pos(0.5, -0.5, -0.5).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
				buffer.pos(0.5, -0.5, -0.7).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
				buffer.pos(0.5, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
				// Front
				buffer.pos(0.5, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
				buffer.pos(0.5, -0.5, -0.7).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
				buffer.pos(0.3, -0.5, -0.7).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
				buffer.pos(0.3, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
				
				buffer.pos(0.3, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
				buffer.pos(0.3, -0.5, -0.7).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
				buffer.pos(0.3, -0.5, -0.5).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
				buffer.pos(0.3, 0.3, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
				
				buffer.pos(0.3, -0.5, -0.7).tex(woodTopSprite.getMinU(), woodTopSprite.getMinV()).endVertex();
				buffer.pos(0.5, -0.5, -0.7).tex(woodTopSprite.getMaxU(), woodTopSprite.getMinV()).endVertex();
				buffer.pos(0.5, -0.5, -0.5).tex(woodTopSprite.getMaxU(), woodTopSprite.getMaxV()).endVertex();
				buffer.pos(0.3, -0.5, -0.5).tex(woodTopSprite.getMinU(), woodTopSprite.getMaxV()).endVertex();
			}
			
			buffer.pos(0.3, 0.5, 0.5).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.5, 0.5).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.5, 0.3).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(0.3, 0.5, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(0.3, 0.3, 0.5).tex(woodSprite.getMaxU(), woodSprite.getMaxV()).endVertex();
			buffer.pos(0.3, 0.3, 0.3).tex(woodSprite.getMaxU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.3, 0.3).tex(woodSprite.getMinU(), woodSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.3, 0.5).tex(woodSprite.getMinU(), woodSprite.getMaxV()).endVertex();
			
			buffer.pos(0.5, 0.5, 0.3).tex(woodTopSprite.getMinU(), woodTopSprite.getMinV()).endVertex();
			buffer.pos(0.5, 0.3, 0.3).tex(woodTopSprite.getMinU(), woodTopSprite.getMaxV()).endVertex();
			buffer.pos(0.3, 0.3, 0.3).tex(woodTopSprite.getMaxU(), woodTopSprite.getMaxV()).endVertex();
			buffer.pos(0.3, 0.5, 0.3).tex(woodTopSprite.getMaxU(), woodTopSprite.getMinV()).endVertex();
			
			buffer.pos(0.3, 0.5, 0.3).tex(woodTopSprite.getMinU(), woodTopSprite.getMinV()).endVertex();
			buffer.pos(0.3, 0.3, 0.3).tex(woodTopSprite.getMinU(), woodTopSprite.getMaxV()).endVertex();
			buffer.pos(0.3, 0.3, 0.5).tex(woodTopSprite.getMaxU(), woodTopSprite.getMaxV()).endVertex();
			buffer.pos(0.3, 0.5, 0.5).tex(woodTopSprite.getMaxU(), woodTopSprite.getMinV()).endVertex();
			t.draw();
		}
		
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
	}
}
