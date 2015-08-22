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
package forestry.core.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import forestry.api.core.IModelObject;
import forestry.api.core.ITileStructure;
import forestry.api.core.IVariantObject;
import forestry.core.proxy.Proxies;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Utils;

public abstract class BlockStructure extends BlockForestry implements IVariantObject {

	public enum EnumStructureState {
		VALID, INVALID, INDETERMINATE
	}

	public BlockStructure(Material material) {
		super(material);
		setHardness(1.0f);
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);

		super.breakBlock(world, pos, state);

		if (tile instanceof ITileStructure) {
			ITileStructure structure = (ITileStructure) tile;
			if (structure.isIntegratedIntoStructure() && !structure.isMaster()) {
				ITileStructure central = structure.getCentralTE();
				if (central != null) {
					central.validateStructure();
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9) {

		if (player.isSneaking()) {
			return false;
		}

		TileForestry tile = (TileForestry) world.getTileEntity(pos);
		if (!Utils.isUseableByPlayer(player, tile)) {
			return false;
		}

		// GUIs can only be opened on integrated structure blocks.
		if (tile instanceof ITileStructure) {
			if (!((ITileStructure) tile).isIntegratedIntoStructure()) {
				return false;
			}
		}

		if (Proxies.common.isSimulating(world)) {
			if (tile.allowsViewing(player)) {
				tile.openGui(player);
			} else {
				player.addChatMessage(new ChatComponentTranslation("for.chat.accesslocked", PlayerUtil.getOwnerName(tile)));
			}
		}
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor) {
		super.onNeighborBlockChange(world, pos, state, neighbor);
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof ITileStructure)) {
			return;
		}

		((ITileStructure) tile).validateStructure();
	}
	
	@Override
	public ModelType getModelType() {
		return ModelType.META;
	}
}
