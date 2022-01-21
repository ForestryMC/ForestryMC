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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.ITree;
import forestry.arboriculture.genetics.Tree;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.RenderUtil;

import genetics.api.alleles.IAllele;

/**
 * This is the base TE class for any block that needs to contain tree genome information.
 *
 * @author SirSengir
 */
public abstract class TileTreeContainer extends BlockEntity implements IStreamable, IOwnedTile {

	@Nullable
	private ITree containedTree;
	private final OwnerHandler ownerHandler = new OwnerHandler();

	public TileTreeContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/* SAVING & LOADING */
	@Override
	public void load(BlockState state, CompoundTag compoundNBT) {
		super.load(state, compoundNBT);

		if (compoundNBT.contains("ContainedTree")) {
			containedTree = new Tree(compoundNBT.getCompound("ContainedTree"));
		}
		ownerHandler.read(compoundNBT);
	}

	@Override
	public CompoundTag save(CompoundTag compoundNBT) {
		compoundNBT = super.save(compoundNBT);

		if (containedTree != null) {
			CompoundTag subcompound = new CompoundTag();
			containedTree.write(subcompound);
			compoundNBT.put("ContainedTree", subcompound);
		}
		ownerHandler.write(compoundNBT);

		return compoundNBT;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		String speciesUID = "";
		ITree tree = getTree();
		if (tree != null) {
			speciesUID = tree.getIdentifier();
		}
		data.writeUtf(speciesUID);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		String speciesUID = data.readUtf();
		ITree tree = getTree(speciesUID);
		setTree(tree);
	}

	private static ITree getTree(String speciesUID) {
		IAllele[] treeTemplate = TreeManager.treeRoot.getTemplates().getTemplate(speciesUID);
		Preconditions.checkArgument(treeTemplate.length > 0, "There is no tree template for speciesUID %s", speciesUID);
		return TreeManager.treeRoot.templateAsIndividual(treeTemplate);
	}

	/* CLIENT INFORMATION */

	/* CONTAINED TREE */
	public void setTree(ITree tree) {
		this.containedTree = tree;
		if (level != null && level.isClientSide) {
			RenderUtil.markForUpdate(getBlockPos());
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
	public abstract void onBlockTick(Level worldIn, BlockPos pos, BlockState state, Random rand);

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, getUpdateTag());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag nbt = pkt.getTag();
		handleUpdateTag(getBlockState(), nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		super.handleUpdateTag(state, tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}

}
