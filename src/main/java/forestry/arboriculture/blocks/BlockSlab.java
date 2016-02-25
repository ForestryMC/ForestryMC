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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWood.WoodMeshDefinition;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.render.ParticleHelper;
import forestry.plugins.PluginArboriculture;

public class BlockSlab extends net.minecraft.block.BlockSlab implements IWoodTyped, IItemModelRegister, ITileEntityProvider {

	private final ParticleHelper.Callback particleCallback;
	private final boolean fireproof;
	
	protected String[] harvestTool;
	protected int[] harvestLevel;

	public BlockSlab(boolean fireproof) {
		super(Material.wood);

		this.fireproof = fireproof;

		harvestTool = new String[EnumWoodType.values().length];
		harvestLevel = new int[harvestTool.length];
		for(int i = 0;i < harvestTool.length;i++){
			harvestLevel[i] = -1;
		}
		
		setCreativeTab(Tabs.tabArboriculture);
		setLightOpacity(0);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setHarvestLevel("axe", 0);
		
        IBlockState iblockstate = this.blockState.getBaseState();

        if (!isDouble())
        {
            iblockstate = iblockstate.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }

        setDefaultState(iblockstate.withProperty(EnumWoodType.WOODTYPE, EnumWoodType.LARCH));

		this.particleCallback = new ParticleHelper.DefaultCallback(this);
	}

	@Override
	protected BlockState createBlockState() {
		return this.isDouble() ? new BlockState(this, new IProperty[] { EnumWoodType.WOODTYPE }): new BlockState(this, new IProperty[] { HALF, EnumWoodType.WOODTYPE });
	}
	
    @Override
	public IBlockState getStateFromMeta(int meta) {
    	if (this == PluginArboriculture.blocks.slabsDouble || this == PluginArboriculture.blocks.slabsDoubleFireproof)
    		return getDefaultState();
        return getDefaultState().withProperty(HALF, EnumBlockHalf.values()[meta]);
    }

    @Override
	public int getMetaFromState(IBlockState state){
    	if(!state.getProperties().containsKey(HALF)) {
    		return 0;
    	}
        return state.getValue(HALF).ordinal();
    }

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileWood) {
			TileWood wood = (TileWood) tile;
			state = state.withProperty(EnumWoodType.WOODTYPE, wood.getWoodType());
		}
		return super.getActualState(state, world, pos);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if(!isFireproof()){
			manager.registerVariant(item, ItemBlockWood.getVariants(this));
		}
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}
	
	@Override
	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(Blocks.wooden_slab, 2, getMetaFromState(state) & 7);
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return "SomeSlab";
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		if (isDouble()) {
			for (EnumWoodType woodType : EnumWoodType.VALUES) {
				list.add(TreeManager.woodItemAccess.getSlab(woodType, fireproof));
			}
		}
	}

	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new TileWood();
	}

	/* PROPERTIES */
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return TileWood.getPickBlock(this, world, pos);
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<ArrayList<ItemStack>> drops = new ThreadLocal<>();
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		drops.set(TileWood.getDrops(this, world, pos));
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = drops.get();
		drops.remove();

		// not harvested, get drops normally
		if (ret == null) {
			ret = TileWood.getDrops(this, world, pos);
		}

		return ret;
	}
	
	@Override
	public float getBlockHardness(World world, BlockPos pos) {
		TileWood wood = TileWood.getWoodTile(world,pos);
		if (wood == null) {
			return EnumWoodType.DEFAULT_HARDNESS;
		}
		return wood.getWoodType().getHardness();
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return !isFireproof();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return isFireproof() ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return isFireproof() ? 0 : 5;
	}

	@Override
	public String getBlockKind() {
		return "slab";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}

	// Minecraft's BlockSlab overrides this for their slabs, so we change it back to normal here
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(this);
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

	@Override
	public IProperty getVariantProperty() {
		return EnumWoodType.WOODTYPE;
	}
	
	@Override
	public boolean isDouble() {
		return false;
	}

	@Override
	public Object getVariant(ItemStack stack) {
		return ItemBlockWood.getWoodType(stack);
	}
	
    @Override
	public void setHarvestLevel(String toolClass, int level, IBlockState state){
        int idx = this.getMetaFromState(state);
        this.harvestTool[idx] = toolClass;
        this.harvestLevel[idx] = level;
    }

    @Override
	public String getHarvestTool(IBlockState state){
        return harvestTool[getMetaFromState(state)];
    }

    @Override
	public int getHarvestLevel(IBlockState state){
        return harvestLevel[getMetaFromState(state)];
    }
}
