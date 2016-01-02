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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Constants;
import forestry.core.entities.EntitySelector;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.EntityUtil;

public abstract class FarmLogic implements IFarmLogic {
	private final EntitySelectorFarm entitySelectorFarm = new EntitySelectorFarm(this);
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

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTextureMap() {
		return TextureMap.locationBlocksTexture;
	}

	public abstract boolean isAcceptedWindfall(ItemStack stack);

	protected final boolean isAirBlock(Block block) {
		return block.getMaterial() == Material.air;
	}

	protected final boolean isWaterSourceBlock(World world, BlockPos position) {
		Block block = world.getBlockState(position).getBlock();
		return block == Blocks.water && block.getMetaFromState(world.getBlockState(position)) == 0;
	}

	protected final BlockPos translateWithOffset(int x, int y, int z, FarmDirection farmDirection, int step) {
		return BlockUtil.multiply(new BlockPos(farmDirection.getFacing().getFrontOffsetX(), farmDirection.getFacing().getFrontOffsetY() , farmDirection.getFacing().getFrontOffsetZ()), step).add(x, y, z);
	}

	protected final void setBlock(BlockPos position, Block block, int meta) {
		getWorld().setBlockState(position, block.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}

	private AxisAlignedBB getHarvestBox(IFarmHousing farmHousing, boolean toWorldHeight) {
		BlockPos coords = new BlockPos(farmHousing.getCoords()[0], farmHousing.getCoords()[1], farmHousing.getCoords()[2]);
		BlockPos area = new BlockPos(farmHousing.getArea()[0], farmHousing.getArea()[1], farmHousing.getArea()[2]);
		BlockPos offset = new BlockPos(farmHousing.getOffset()[0], farmHousing.getOffset()[1], farmHousing.getOffset()[2]);

		BlockPos min = coords.add(offset);
		BlockPos max = min.add(area);

		int maxY = max.getY();
		if (toWorldHeight) {
			maxY = getWorld().getHeight();
		}

		return AxisAlignedBB.fromBounds(min.getX(), min.getY(), min.getZ(), max.getX(), maxY, max.getZ());
	}

	protected List<ItemStack> collectEntityItems(boolean toWorldHeight) {
		AxisAlignedBB harvestBox = getHarvestBox(housing, toWorldHeight);

		List<EntityItem> entityItems = EntityUtil.selectEntitiesWithinAABB(housing.getWorld(), entitySelectorFarm, harvestBox);
		List<ItemStack> stacks = new ArrayList<>();
		for (EntityItem entity : entityItems) {
			ItemStack contained = entity.getEntityItem();
			stacks.add(contained.copy());
			entity.setDead();
		}
		return stacks;
	}

	private static class EntitySelectorFarm extends EntitySelector<EntityItem> {
		private final FarmLogic farmLogic;

		public EntitySelectorFarm(FarmLogic farmLogic) {
			super(EntityItem.class);
			this.farmLogic = farmLogic;
		}

		@Override
		protected boolean isEntityApplicableTyped(EntityItem entity) {
			if (entity.isDead) {
				return false;
			}

			ItemStack contained = entity.getEntityItem();
			return farmLogic.isAcceptedGermling(contained) || farmLogic.isAcceptedWindfall(contained);
		}
	}
	
	@Override
	public TextureAtlasSprite getSprite() {
		IBakedModel iBakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(getIconStack());
		TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(iBakedModel.getTexture().getIconName());
		return textureAtlasSprite;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconStack() {
		return new ItemStack(getIconItem(), 1, getIconMetadata());
	}

	@SideOnly(Side.CLIENT)
	public abstract Item getIconItem();

	@SideOnly(Side.CLIENT)
	public int getIconMetadata() {
		return 0;
	}
}
