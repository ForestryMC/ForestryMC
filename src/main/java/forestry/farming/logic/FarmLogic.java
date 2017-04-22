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

import javax.annotation.Nullable;
import java.util.List;

import com.google.common.base.Predicate;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.utils.VectUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public abstract ItemStack getIconItemStack();

	public abstract boolean isAcceptedWindfall(ItemStack stack);

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

	protected NonNullList<ItemStack> collectEntityItems(World world, IFarmHousing farmHousing, boolean toWorldHeight) {
		AxisAlignedBB harvestBox = getHarvestBox(world, farmHousing, toWorldHeight);

		List<EntityItem> entityItems = world.getEntitiesWithinAABB(EntityItem.class, harvestBox, entitySelectorFarm);
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (EntityItem entity : entityItems) {
			ItemStack contained = entity.getEntityItem();
			stacks.add(contained.copy());
			entity.setDead();
		}
		return stacks;
	}

	private static class EntitySelectorFarm implements Predicate<EntityItem> {
		private final FarmLogic farmLogic;

		public EntitySelectorFarm(FarmLogic farmLogic) {
			this.farmLogic = farmLogic;
		}

		@Override
		public boolean apply(@Nullable EntityItem entity) {
			if (entity == null || entity.isDead) {
				return false;
			}

			if (entity.getEntityData().getBoolean("PreventRemoteMovement")) {
				return false;
			}

			ItemStack contained = entity.getEntityItem();
			return farmLogic.isAcceptedGermling(contained) || farmLogic.isAcceptedWindfall(contained);
		}
	}

	// for debugging
	@Override
	public String toString() {
		return getName();
	}
}
