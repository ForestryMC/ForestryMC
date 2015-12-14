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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Constants;
import forestry.core.entities.EntitySelector;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.vect.Vect;

public abstract class FarmLogic implements IFarmLogic {
	private final EntitySelectorFarm entitySelectorFarm = new EntitySelectorFarm(this);
	protected final IFarmHousing housing;
	protected boolean isManual;

	protected FarmLogic(IFarmHousing housing) {
		this.housing = housing;
	}

	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}

	protected World getWorld() {
		return housing.getWorld();
	}

	@Override
	public ResourceLocation getSpriteSheet() {
		return SpriteSheet.ITEMS.getLocation();
	}

	public abstract boolean isAcceptedWindfall(ItemStack stack);

	protected final boolean isAirBlock(Block block) {
		return block.getMaterial() == Material.air;
	}

	protected final boolean isWaterSourceBlock(World world, Vect position) {
		return world.getBlock(position.x, position.y, position.z) == Blocks.water &&
				world.getBlockMetadata(position.x, position.y, position.z) == 0;
	}

	protected final Vect translateWithOffset(int x, int y, int z, FarmDirection farmDirection, int step) {
		return new Vect(farmDirection.getForgeDirection()).multiply(step).add(x, y, z);
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		getWorld().setBlock(position.x, position.y, position.z, block, meta, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}

	private AxisAlignedBB getHarvestBox(IFarmHousing farmHousing, boolean toWorldHeight) {
		Vect coords = new Vect(farmHousing.getCoords());
		Vect area = new Vect(farmHousing.getArea());
		Vect offset = new Vect(farmHousing.getOffset());

		Vect min = coords.add(offset);
		Vect max = min.add(area);

		int maxY = max.y;
		if (toWorldHeight) {
			maxY = getWorld().getHeight();
		}

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, maxY, max.z);
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
