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
package forestry.farming.logic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.sprite.ISprite;
import forestry.api.core.sprite.Sprite;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.vect.Vect;

public abstract class FarmLogic implements IFarmLogic {

	protected final IFarmHousing housing;

	protected boolean isManual;

	protected FarmLogic(IFarmHousing housing) {
		this.housing = housing;
	}

	@Override
	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}

	protected World getWorld() {
		return housing.getWorld();
	}

	protected final boolean isAirBlock(Block block) {
		return block.getMaterial() == Material.air;
	}

	protected final boolean isWaterSourceBlock(World world, Vect position) {
		IBlockState state = world.getBlockState(position.pos);
		return state.getBlock() == Blocks.water &&
				state.getBlock().getMetaFromState(state) == 0;
	}

	protected final Vect translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return new Vect(farmDirection.getDirection()).multiply(step).add(pos);
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		getWorld().setBlockState(position.pos, block.getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ISprite getIcon() {
		IBakedModel iBakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(getIconStack());
		TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(iBakedModel.getTexture().getIconName());
		return new Sprite(textureAtlasSprite);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconStack() {
		return new ItemStack(getIconItem(), 1, getIconMetadata());
	}
	
	@SideOnly(Side.CLIENT)
	public abstract Item getIconItem();
	
	@SideOnly(Side.CLIENT)
	public int getIconMetadata(){
		return 0;
	}
}
