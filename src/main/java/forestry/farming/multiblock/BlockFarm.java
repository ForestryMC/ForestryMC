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
package forestry.farming.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.BlockStructure;
import forestry.core.gadgets.UnlistedBlockAccess;
import forestry.core.gadgets.UnlistedBlockPos;
import forestry.core.proxy.Proxies;
import forestry.core.render.ParticleHelper;
import forestry.core.render.ParticleHelperCallback;
import forestry.core.utils.StackUtils;

public class BlockFarm extends BlockStructure {

	public static final PropertyEnum META = PropertyEnum.create("meta", FarmType.class);
	private static BlockFarm instance;

	private enum FarmType implements IStringSerializable {
		PLAIN, PLAIN_2, GEARS, HATCH, VALVE, CONTROL;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

	}

	public BlockFarm() {
		super(Material.rock);
		setHardness(1.0f);
		instance = this;
		setDefaultState(blockState.getBaseState().withProperty(META, FarmType.PLAIN));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(META, FarmType.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((FarmType) state.getValue(META)).ordinal();
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
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
		if (drops.isEmpty()) {
			return super.getPickBlock(target, world, pos, player);
		}
		return drops.get(0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving,
			ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, entityLiving, stack);
		if (!stack.hasTagCompound()) {
			return;
		}

		TileFarm tile = (TileFarm) world.getTileEntity(pos);
		tile.setFarmBlockTexture(EnumFarmBlockTexture.getFromCompound(stack.getTagCompound()));
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		int meta = getMetaFromState(world.getBlockState(pos));
		if (Proxies.common.isSimulating(world) && canHarvestBlock(world, pos, player)) {
			List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
			for (ItemStack drop : drops) {
				StackUtils.dropItemStackAsEntity(drop, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return world.setBlockToAir(pos);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		int meta = getMetaFromState(world.getBlockState(pos));
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
			return new TileGearbox();
		case 3:
			return new TileHatch();
		case 4:
			return new TileValve();
		case 5:
			return new TileControl();
		default:
			return new TileFarmPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, getStateFromMeta(meta));
	}

	/* ICONS */
	@Override
	public int getRenderType() {
		return 3;
	}

	@SideOnly(Side.CLIENT)
	public static void registerBlockIcons() {
		EnumFarmBlockTexture.registerIcons();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation("forestry:ffarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 1,
				new ModelResourceLocation("forestry:ffarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 2,
				new ModelResourceLocation("forestry:ffarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 3,
				new ModelResourceLocation("forestry:ffarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 4,
				new ModelResourceLocation("forestry:ffarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 5,
				new ModelResourceLocation("forestry:ffarm", "inventory"));
	}

	private static class ParticleCallback implements ParticleHelperCallback {

		@Override
		@SideOnly(Side.CLIENT)
		public void addHitEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta) {
			setTexture(fx, world, x, y, z);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addDestroyEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta) {
			setTexture(fx, world, x, y, z);
		}

		@SideOnly(Side.CLIENT)
		private static void setTexture(EntityDiggingFX fx, World world, int x, int y, int z) {
			fx.setParticleIcon(EnumFarmBlockTexture
					.getIcon(ForestryBlock.farm.block().getMetaFromState(world.getBlockState(new BlockPos(x, y, z))))
					.getSprite());
		}
	}

	private static final ParticleHelperCallback callback = new ParticleCallback();

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		return ParticleHelper.addHitEffects(worldObj, instance, target, effectRenderer, callback);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
		return ParticleHelper.addDestroyEffects(world, instance, pos, world.getBlockState(pos), effectRenderer,
				callback);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getMetaFromState(world.getBlockState(pos)) == 5;
	}

}
