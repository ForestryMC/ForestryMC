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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.entities.EntitySelector;
import forestry.core.utils.VectUtil;

public abstract class FarmLogic implements IFarmLogic {
	private final EntitySelectorFarm entitySelectorFarm = new EntitySelectorFarm(this);
	protected boolean isManual;

	@Override
	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTextureMap() {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getStack() {
		return new ItemStack(getItem(), 1, getMetadata());
	}

	@SideOnly(Side.CLIENT)
	public abstract Item getItem();

	@SideOnly(Side.CLIENT)
	public int getMetadata() {
		return 0;
	}

	public abstract boolean isAcceptedWindfall(ItemStack stack);

	@Deprecated
	protected final boolean isAirBlock(@Nonnull Block block, @Nonnull IBlockState blockState, @Nonnull World world, @Nonnull BlockPos blockPos) {
		return block.isAir(blockState, world, blockPos);
	}

	protected final boolean isWaterSourceBlock(World world, BlockPos position) {
		IBlockState blockState = world.getBlockState(position);
		Block block = blockState.getBlock();
		return block == Blocks.WATER;
	}
	
	protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return VectUtil.scale(farmDirection.getFacing().getDirectionVec(), step).add(pos);
	}

	private static AxisAlignedBB getHarvestBox(World world, IFarmHousing farmHousing, boolean toWorldHeight) {
		BlockPos coords = farmHousing.getCoords();
		Vec3i area = farmHousing.getArea();
		Vec3i offset = farmHousing.getOffset();

		BlockPos min = coords.add(offset);
		BlockPos max = min.add(area);

		int maxY = max.getY();
		if (toWorldHeight) {
			maxY = world.getHeight();
		}

		return new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), maxY, max.getZ());
	}

	protected List<ItemStack> collectEntityItems(World world, IFarmHousing farmHousing, boolean toWorldHeight) {
		AxisAlignedBB harvestBox = getHarvestBox(world, farmHousing, toWorldHeight);

		List<EntityItem> entityItems = world.getEntitiesWithinAABB(entitySelectorFarm.getEntityClass(), harvestBox, entitySelectorFarm);
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
}
