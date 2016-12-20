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
package forestry.arboriculture.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Random;

import com.google.common.base.Preconditions;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.genetics.Tree;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.NBTUtilForestry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This is the base TE class for any block that needs to contain tree genome information.
 *
 * @author SirSengir
 */
public abstract class TileTreeContainer extends TileEntity implements IStreamable, IOwnedTile {

	@Nullable
	private ITree containedTree;
	private final OwnerHandler ownerHandler = new OwnerHandler();

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("ContainedTree")) {
			containedTree = new Tree(nbttagcompound.getCompoundTag("ContainedTree"));
		}
		ownerHandler.readFromNBT(nbttagcompound);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		if (containedTree != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			containedTree.writeToNBT(subcompound);
			nbttagcompound.setTag("ContainedTree", subcompound);
		}
		ownerHandler.writeToNBT(nbttagcompound);

		return nbttagcompound;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		String speciesUID = "";
		ITree tree = getTree();
		if (tree != null) {
			speciesUID = tree.getIdent();
		}
		data.writeString(speciesUID);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		String speciesUID = data.readString();
		ITree tree = getTree(speciesUID);
		setTree(tree);
	}

	private static ITree getTree(String speciesUID) {
		IAllele[] treeTemplate = TreeManager.treeRoot.getTemplate(speciesUID);
		Preconditions.checkArgument(treeTemplate != null, "There is no tree template for speciesUID %s", speciesUID);
		return TreeManager.treeRoot.templateAsIndividual(treeTemplate);
	}

	/* CLIENT INFORMATION */

	/* CONTAINED TREE */
	public void setTree(ITree tree) {
		this.containedTree = tree;
		if (world != null && world.isRemote) {
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Nullable
	public ITree getTree() {
		return this.containedTree;
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* UPDATING */

	/**
	 * Leaves and saplings will implement their logic here.
	 */
	public abstract void onBlockTick(World worldIn, BlockPos pos, IBlockState state, Random rand);

	/**
	 * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
	 * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return !Block.isEqualTo(oldState.getBlock(), newSate.getBlock());
	}

	@Nonnull
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbt = pkt.getNbtCompound();
		handleUpdateTag(nbt);
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}

}
