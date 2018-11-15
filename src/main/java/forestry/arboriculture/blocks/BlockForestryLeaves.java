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
package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.IToolScoop;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;

public class BlockForestryLeaves extends BlockAbstractLeaves implements ITileEntityProvider, IGrowable {

	public BlockForestryLeaves() {
		setDefaultState(this.blockState.getBaseState()
			.withProperty(CHECK_DECAY, false)
			.withProperty(DECAYABLE, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
			new IProperty[]{DECAYABLE, CHECK_DECAY},
			new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS}
		);
	}

	@Override
	protected ITree getTree(IBlockAccess world, BlockPos pos) {
		TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leaves != null) {
			ITree tree = leaves.getTree();
			if (tree != null) {
				return tree;
			}
		}

		return null;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		TileLeaves tileLeaves = TileUtil.getTile(world, pos, TileLeaves.class);

		// check leaves tile because they might have decayed
		if (tileLeaves != null && !tileLeaves.isInvalid() && rand.nextFloat() <= 0.1) {
			tileLeaves.onBlockTick(world, pos, state, rand);
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		if (!state.getValue(DECAYABLE)) {
			i |= DECAYABLE_FLAG;
		}

		if (state.getValue(CHECK_DECAY)) {
			i |= CHECK_DECAY_FLAG;
		}

		return i;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(DECAYABLE, (meta & DECAYABLE_FLAG) == 0).withProperty(CHECK_DECAY, (meta & CHECK_DECAY_FLAG) > 0);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
			.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	/* TILE ENTITY */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileLeaves();
	}

	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, World world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune) {
		TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (tile == null) {
			return;
		}

		ITree tree = tile.getTree();
		if (tree == null) {
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);

		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(TreeManager.treeRoot.getMemberStack(sapling, EnumGermlingType.SAPLING));
			}
		}

		// Add fruits
		if (tile.hasFruit()) {
			drops.addAll(tree.produceStacks(world, pos, tile.getRipeningTime()));
		}
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:leaves", "inventory"));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leaves != null) {
			IButterfly caterpillar = leaves.getCaterpillar();
			ItemStack heldItem = player.getHeldItem(hand);
			ItemStack otherHand = player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
			if (heldItem.isEmpty() && otherHand.isEmpty()) {
				if (leaves.hasFruit() && leaves.getRipeness() >= 0.9F) {
					PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, state);
					NetworkUtil.sendNetworkPacket(packet, pos, world);
					for (ItemStack fruit : leaves.pickFruit(ItemStack.EMPTY)) {
						ItemHandlerHelper.giveItemToPlayer(player, fruit);
					}
					return true;
				}
			} else if (heldItem.getItem() instanceof IToolScoop && caterpillar != null) {
				ItemStack butterfly = ButterflyManager.butterflyRoot.getMemberStack(caterpillar, EnumFlutterType.CATERPILLAR);
				ItemStackUtil.dropItemStackAsEntity(butterfly, world, pos);
				leaves.setCaterpillar(null);
				return true;
			}
		}

		return false;
	}

	/* IGrowable */

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
		return leafTile != null && leafTile.hasFruit() && leafTile.getRipeness() < 1.0f;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leafTile != null) {
			leafTile.addRipeness(0.5f);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (worldIn != null && pos != null) {
			TileLeaves leaves = TileUtil.getTile(worldIn, pos, TileLeaves.class);
			if (leaves != null) {
				if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
					return leaves.getFruitColour();
				} else {
					EntityPlayer thePlayer = Minecraft.getMinecraft().player;
					return leaves.getFoliageColour(thePlayer);
				}
			}
		}
		return ModuleArboriculture.proxy.getFoliageColorBasic();
	}
}
