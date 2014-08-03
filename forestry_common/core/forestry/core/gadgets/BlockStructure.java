/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import forestry.api.core.ITileStructure;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public abstract class BlockStructure extends BlockForestry {

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
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity tile = world.getTileEntity(x, y, z);

		super.breakBlock(world, x, y, z, block, meta);

		if (tile instanceof ITileStructure) {
			ITileStructure structure = (ITileStructure) tile;
			if (structure.isIntegratedIntoStructure() && !structure.isMaster()) {
				ITileStructure central = structure.getCentralTE();
				if (central != null)
					central.validateStructure();
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {

		if (player.isSneaking())
			return false;

		TileForestry tile = (TileForestry) world.getTileEntity(x, y, z);
		if (!tile.isUseableByPlayer(player))
			return false;

		if (!Proxies.common.isSimulating(world))
			return player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IStructureBlockItem);

		// GUIs can only be opened on integrated structure blocks.
		if (tile instanceof ITileStructure)
			if (!((ITileStructure) tile).isIntegratedIntoStructure())
				return false;

		if (tile.allowsInteraction(player)) {
			tile.openGui(player);
		}
		else {
			String ownerName = StringUtil.localize("gui.derelict");
			
			if (tile.getOwnerProfile() != null)
				ownerName = tile.getOwnerProfile().getName();

			player.addChatMessage(new ChatComponentTranslation("\u00A7c%s %s", new ChatComponentText(ownerName), new ChatComponentTranslation("chat.accesslocked")));
		}
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof ITileStructure))
			return;

		((ITileStructure) tile).validateStructure();
	}
}
