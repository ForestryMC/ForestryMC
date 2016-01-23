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
package forestry.farming.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.core.blocks.BlockStructure;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.render.ParticleHelper;
import forestry.core.render.ParticleHelper.DefaultCallback;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.farming.tiles.TileFarm;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;

public class BlockFarm extends BlockStructure {

	private final ParticleHelper.Callback particleCallback;

	public static final PropertyEnum META = PropertyEnum.create("meta", EnumFarmBlockType.class);
	
	public BlockFarm() {
		super(Material.rock);
		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		this.particleCallback = new FarmCallback(this);
		setDefaultState(blockState.getBaseState().withProperty(META, EnumFarmBlockType.PLAIN));
	}
	

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(META, EnumFarmBlockType.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFarmBlockType) state.getValue(META)).ordinal();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
				.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { META },
				new IUnlistedProperty[] { UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS });
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 6; i++) {
			if (i == 1) {
				continue;
			}

			for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
				ItemStack stack = new ItemStack(item, 1, i);
				NBTTagCompound compound = new NBTTagCompound();
				block.saveToCompound(compound);
				stack.setTagCompound(compound);
				list.add(stack);
			}
		}
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		List<ItemStack> drops = getDrops(world, pos, BlockUtil.getBlockState(world, pos), 0);
		if (drops.isEmpty()) {
			return super.getPickBlock(target, world, pos, player);
		}
		return drops.get(0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		if (!stack.hasTagCompound()) {
			return;
		}

		TileFarm tile = (TileFarm) world.getTileEntity(pos);
		tile.setFarmBlockTexture(EnumFarmBlockTexture.getFromCompound(stack.getTagCompound()));
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		int meta = BlockUtil.getBlockMetadata(world, pos);
		if (!world.isRemote && canHarvestBlock(world, pos, player)) {
			List<ItemStack> drops = getDrops(world, pos, BlockUtil.getBlockState(world, pos), 0);
			for (ItemStack drop : drops) {
				ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
			}
		}
		return world.setBlockToAir(pos);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		int meta = getMetaFromState(state);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileFarm) {
			TileFarm farm = (TileFarm) tile;

			ItemStack stack = new ItemStack(this, 1, meta != 1 ? meta : 0);
			NBTTagCompound compound = new NBTTagCompound();
			farm.getFarmBlockTexture().saveToCompound(compound);
			stack.setTagCompound(compound);
			drops.add(stack);
		}
		return drops;
	}

	@Override
	public int getDamageValue(World world, BlockPos pos) {
		int meta = getMetaFromState(world.getBlockState(pos));
		return meta != 1 ? meta : 0;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch (getMetaFromState(state)) {
			case 2:
				return new TileFarmGearbox();
			case 3:
				return new TileFarmHatch();
			case 4:
				return new TileFarmValve();
			case 5:
				return new TileFarmControl();
			default:
				return new TileFarmPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, getStateFromMeta(meta));
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:ffarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation("forestry:ffarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation("forestry:ffarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 3, new ModelResourceLocation("forestry:ffarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 4, new ModelResourceLocation("forestry:ffarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 5, new ModelResourceLocation("forestry:ffarm", "inventory"));
	}

	/* Particles */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		return ParticleHelper.addHitEffects(worldObj, this, target, effectRenderer, particleCallback);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
		return ParticleHelper.addDestroyEffects(world, this, world.getBlockState(pos), pos, effectRenderer, particleCallback);
	}
	
	public class FarmCallback extends DefaultCallback{

		public FarmCallback(Block block) {
			super(block);
		}
		
		@Override
		protected void setTexture(EntityDiggingFX fx, BlockPos pos, IBlockState state) {
			IExtendedBlockState extend = (IExtendedBlockState) state;
			TileFarm farm = (TileFarm) fx.getEntityWorld().getTileEntity(pos);
			
			fx.setParticleIcon(EnumFarmBlockTexture.getSprite(farm.getFarmBlockTexture(), 2));
		}
		
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getMetaFromState(world.getBlockState(pos)) == 5;
	}

	public ItemStack get(EnumFarmBlockType type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
