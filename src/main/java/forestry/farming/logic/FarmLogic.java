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
import net.minecraft.client.renderer.texture.TextureMap;
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
import forestry.core.utils.vect.Vect;

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

	protected final boolean isAirBlock(Block block) {
		return block.getMaterial() == Material.air;
	}

	protected final boolean isWaterSourceBlock(World world, Vect position) {
		return BlockUtil.getBlock(world, position) == Blocks.water &&
				BlockUtil.getBlockMetadata(world, position) == 0;
	}

	protected final Vect translateWithOffset(int x, int y, int z, FarmDirection farmDirection, int step) {
		return new Vect(farmDirection.getForgeDirection()).multiply(step).add(x, y, z);
	}
	
	protected final Vect translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return new Vect(farmDirection.getForgeDirection()).multiply(step).add(pos);
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		getWorld().setBlockState(position, block.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}

	private AxisAlignedBB getHarvestBox(IFarmHousing farmHousing, boolean toWorldHeight) {
		Vect coords = new Vect(farmHousing.getCoords());
		Vect area = new Vect(farmHousing.getArea());
		Vect offset = new Vect(farmHousing.getOffset());

		Vect min = coords.add(offset);
		Vect max = min.add(area);

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
}
