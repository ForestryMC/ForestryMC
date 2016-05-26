package forestry.arboriculture.blocks;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.multiblock.ICharcoalPileComponent;
import forestry.apiculture.blocks.BlockCandle;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.multiblock.EnumPilePosition;
import forestry.arboriculture.multiblock.ICharcoalPileControllerInternal;
import forestry.arboriculture.render.PileParticleCallback;
import forestry.arboriculture.render.PileStateMapper;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.PluginCore;
import forestry.core.blocks.BlockStructure;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.multiblock.MultiblockLogic;
import forestry.core.proxy.Proxies;
import forestry.core.render.ParticleHelper;
import forestry.core.tiles.TileUtil;

public abstract class BlockPile extends BlockStructure implements ITileEntityProvider, IStateMapperRegister, ISpriteRegister {

    /**
     * B: .. T: x.
     * B: .. T: x.
     */
    protected static final AxisAlignedBB AABB_QTR_TOP_WEST = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 1.0D);
    /**
     * B: .. T: .x
     * B: .. T: .x
     */
    protected static final AxisAlignedBB AABB_QTR_TOP_EAST = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    /**
     * B: .. T: xx
     * B: .. T: ..
     */
    protected static final AxisAlignedBB AABB_QTR_TOP_NORTH = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    /**
     * B: .. T: ..
     * B: .. T: xx
     */
    protected static final AxisAlignedBB AABB_QTR_TOP_SOUTH = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    /**
     * B: .. T: x.
     * B: .. T: ..
     */
    protected static final AxisAlignedBB AABB_OCT_TOP_NW = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D);
    /**
     * B: .. T: .x
     * B: .. T: ..
     */
    protected static final AxisAlignedBB AABB_OCT_TOP_NE = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    /**
     * B: .. T: ..
     * B: .. T: x.
     */
    protected static final AxisAlignedBB AABB_OCT_TOP_SW = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D);
    /**
     * B: .. T: ..
     * B: .. T: .x
     */
    protected static final AxisAlignedBB AABB_OCT_TOP_SE = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    /**
     * B: xx T: ..
     * B: xx T: ..
     */
    protected static final AxisAlignedBB AABB_SLAB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    
	public static final PropertyEnum<EnumPilePosition> PILE_POSITION = PropertyEnum.create("position", EnumPilePosition.class);
	public final ParticleHelper.Callback particleCallback;
	
	public static Map<EnumPileType, BlockPile> create() {
		Map<EnumPileType, BlockPile> blockMap = new EnumMap<>(EnumPileType.class);
		for (final EnumPileType type : EnumPileType.VALUES) {
			BlockPile pile = new BlockPile() {
				
				@Override
				public String getHarvestTool(IBlockState state) {
					if(type == EnumPileType.WOOD){
						return "axe";
					}else{
						return "shovel";
					}
				}
				
				@Override
				public int getHarvestLevel(IBlockState state) {
					return 0;
				}
				
				@Override
				public EnumPileType getPileType() {
					return type;
				}
				
				@Override
				public SoundType getSoundType() {
					if(type == EnumPileType.DIRT){
						return Blocks.DIRT.getSoundType();
					}else if(type == EnumPileType.ASH){
						return Blocks.SAND.getSoundType();
					}else if(type == EnumPileType.WOOD){
						return Blocks.LOG.getSoundType();
					}
					return super.getSoundType();
				}
			};
			blockMap.put(type, pile);
		}
		return blockMap;
	}
	
	public BlockPile() {
		super(Material.GROUND);
		setHardness(1.0F);
		setUnlocalizedName("charcoal.pile");
		setCreativeTab(Tabs.tabArboriculture);
		setDefaultState(blockState.getBaseState().withProperty(PILE_POSITION, EnumPilePosition.INTERIOR));
		
		particleCallback = new PileParticleCallback(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{PILE_POSITION}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
				.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}
	
    @Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
        List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(blockState, worldIn, pos)))
        {
            list.add(this.rayTrace(pos, start, end, axisalignedbb));
        }

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0D;

        for (RayTraceResult raytraceresult : list)
        {
            if (raytraceresult != null)
            {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }
	
    @Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn){
        state = this.getActualState(state, worldIn, pos);

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state)){
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
    }

    private static List<AxisAlignedBB> getCollisionBoxList(IBlockState state){
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        EnumPilePosition position = state.getValue(PILE_POSITION);
        
        if(position != EnumPilePosition.INTERIOR){
	        list.add(AABB_SLAB_BOTTOM);
	
	        switch (position) {
				case BACK:
					list.add(AABB_QTR_TOP_SOUTH);
					break;
				case FRONT:
					 list.add(AABB_QTR_TOP_NORTH);
					break;
				case SIDE_LEFT:
					list.add(AABB_QTR_TOP_EAST);
					break;
				case SIDE_RIGHT:
					list.add(AABB_QTR_TOP_WEST);
					break;
				case CORNER_BACK_LEFT:
					list.add(AABB_OCT_TOP_NE);
					break;
				case CORNER_BACK_RIGHT:
					list.add(AABB_OCT_TOP_NW);
					break;
				case CORNER_FRONT_LEFT:
					list.add(AABB_OCT_TOP_SE);
					break;
				case CORNER_FRONT_RIGHT:
					list.add(AABB_OCT_TOP_SW);
					break;
				default:
					break;
			}
        }else{
        	list.add(Block.FULL_BLOCK_AABB);
        }

        return list;
    }
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(PILE_POSITION).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(PILE_POSITION, EnumPilePosition.values()[meta]);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TilePile();
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ICharcoalPileComponent) {
			ICharcoalPileComponent kiln = (ICharcoalPileComponent) tile;
			if (kiln.getMultiblockLogic().isConnected() && kiln.getMultiblockLogic().getController().isAssembled() && kiln.getMultiblockLogic().getController().isActive()) {
				return 10;
			}
		}
		return super.getLightValue(state, world, pos);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		TilePile pile = TileUtil.getTile(world, pos, TilePile.class);
		if(pile == null){
			return;
		}
		MultiblockLogic<ICharcoalPileControllerInternal> logic = pile.getMultiblockLogic();
		
		if (logic.isConnected() && logic.getController().isAssembled() && logic.getController().isActive() && state.getValue(PILE_POSITION) != EnumPilePosition.INTERIOR) {
			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = rand.nextFloat() * 0.6F - 0.3F;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f3 - 0.5, f1 + 0.5, f2 + f4, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
		if(getPileType() == EnumPileType.WOOD){
			List<ItemStack> woodPiles = new ArrayList();
			for(ITree tree : TreeManager.treeRoot.getIndividualTemplates()) {
				woodPiles.add(createWoodPile(tree));
			}
			subItems.addAll(woodPiles);
		}else if(getPileType() == EnumPileType.DIRT){
			super.getSubBlocks(item, tab, subItems);
		}
	}
	
	private long previousMessageTick = 0;
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote){
			return false;
		}
		
		if (player.isSneaking()) {
			return false;
		}

		TilePile pile = TileUtil.getTile(world, pos, TilePile.class);

		if(pile == null) {
			return false;
		}
		
		ICharcoalPileControllerInternal controller = pile.getMultiblockLogic().getController();

		// If the player's hands are empty and they right-click on a multiblock, they get a
		// multiblock-debugging message if the machine is not assembled.
		if (heldItem == null) {
			if (controller != null) {
				if (!controller.isAssembled()) {
					String validationError = controller.getLastValidationError();
					if (validationError != null) {
						long tick = world.getTotalWorldTime();
						if (tick > previousMessageTick + 20) {
							player.addChatMessage(new TextComponentString(validationError));
							previousMessageTick = tick;
						}
						return true;
					}
				}
			} else {
				player.addChatMessage(new TextComponentTranslation("for.multiblock.error.notConnected"));
				return true;
			}
		}else if(BlockCandle.lightingItems.contains(heldItem.getItem())) {
			if (pile.getMultiblockLogic().isConnected() && controller != null && controller.isAssembled() && !controller.isActive()) {
				controller.setActive(true);
				return true;
			}
		}
		return false;
	}
	
	/* DROP HANDLING */
	// Hack: 	When harvesting we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<List<ItemStack>> drop = new ThreadLocal<>();
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof ICharcoalPileComponent) {
				ICharcoalPileComponent pile = (ICharcoalPileComponent) tile;
				ArrayList<ItemStack> list = new ArrayList<ItemStack>();
				if (getPileType() == EnumPileType.ASH) {
					if(pile.getTree() != null){
						ItemStack charcoal = new ItemStack(PluginArboriculture.items.charcoal, pile.getTree().getGenome().getCarbonization(), pile.getTree().getGenome().getCombustibility());
						list.add(charcoal);
						list.add(new ItemStack(PluginCore.items.ash, 3));
					}else{
						list.add(new ItemStack(Blocks.DIRT, 2));
						list.add(new ItemStack(PluginCore.items.ash, 2));
					}
				} else if (getPileType() == EnumPileType.DIRT) {
					list.add(new ItemStack(this));
				}	else {
					list.add(createWoodPile(pile.getTree()));
				}
				drop.set(list);
			}
		}
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = drop.get();
		drop.remove();
		return drops;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		//use to override world.removeTileEntity
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		if(state.getValue(PILE_POSITION) != EnumPilePosition.INTERIOR){
			return false;
		}
		return true;
	}
	
	@Override
	public int getLightOpacity(IBlockState state) {
		if(state.getValue(PILE_POSITION) != EnumPilePosition.INTERIOR){
			return 0;
		}
		return super.getLightOpacity(state);
	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		if(state.getValue(PILE_POSITION) != EnumPilePosition.INTERIOR){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		if(state.getValue(PILE_POSITION) != EnumPilePosition.INTERIOR){
			return false;
		}
		return true;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if(state.getValue(PILE_POSITION) != EnumPilePosition.INTERIOR){
			return EnumBlockRenderType.INVISIBLE;
		}
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new PileStateMapper());
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		if(getPileType() == EnumPileType.WOOD){
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof ICharcoalPileComponent) {
				return createWoodPile(((ICharcoalPileComponent) tile).getTree());
			}
		}else if(getPileType() == EnumPileType.DIRT){
			return super.getPickBlock(state, target, world, pos, player);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if(getPileType() == EnumPileType.WOOD){
			manager.registerItemModel(item, 0, "woodPile");
		}else if(getPileType() == EnumPileType.DIRT){
			manager.registerItemModel(item, 0, "dirtPile");
		}else if(getPileType() == EnumPileType.ASH){
			manager.registerItemModel(item, 0, "ashPile");
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprites(ITextureManager manager) {
		for(IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()){
			if(allele instanceof IAlleleTreeSpecies){
				IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) allele;
				treeSpecies.getWoodProvider().registerSprites(Item.getItemFromBlock(this), manager);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager effectRenderer) {
		return ParticleHelper.addBlockHitEffects(worldObj, target.getBlockPos(), target.sideHit, effectRenderer, particleCallback);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
		IBlockState blockState = world.getBlockState(pos);
		return ParticleHelper.addDestroyEffects(world, this, blockState, pos, effectRenderer, particleCallback);
	}
	
	public static ITree getTree(ItemStack stack){
		if (stack != null) {
			return new Tree(stack.getTagCompound().getCompoundTag("ContainedTree"));
		}
		return null;
	}
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if(getPileType() == EnumPileType.WOOD){
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof ICharcoalPileComponent) {
				
				ITree tree = ((ICharcoalPileComponent)tile).getTree();
				if(tree != null){
					ItemStack wood = tree.getGenome().getPrimary().getWoodProvider().getWoodStack();
					if(wood != null){
						Block block = Block.getBlockFromItem(wood.getItem());
						return block.getFlammability(world, pos, face);
					}
				}
			}
		}
		return super.getFlammability(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if(getPileType() == EnumPileType.WOOD){
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof ICharcoalPileComponent) {
				
				ITree tree = ((ICharcoalPileComponent)tile).getTree();
				if(tree != null){
					ItemStack wood = tree.getGenome().getPrimary().getWoodProvider().getWoodStack();
					if(wood != null){
						Block block = Block.getBlockFromItem(wood.getItem());
						return block.getFireSpreadSpeed(world, pos, face);
					}
				}
			}
		}
		return super.getFireSpreadSpeed(world, pos, face);
		
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
		if(getPileType() == EnumPileType.WOOD){
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof ICharcoalPileComponent) {
				
				ITree tree = ((ICharcoalPileComponent)tile).getTree();
				if(tree != null){
					ItemStack wood = tree.getGenome().getPrimary().getWoodProvider().getWoodStack();
					if(wood != null){
						Block block = Block.getBlockFromItem(wood.getItem());
						return block.getStateFromMeta(wood.getMetadata()).getBlockHardness(world, pos);
					}
				}
			}
		}
		return super.getBlockHardness(blockState, world, pos);
	}
	
	public static ItemStack createWoodPile(ITree tree) {
		if (tree != null) {
			ItemStack stack = new ItemStack(PluginArboriculture.blocks.piles.get(EnumPileType.WOOD));
			NBTTagCompound nbtTag = new NBTTagCompound();
			tree.writeToNBT(nbtTag);
			NBTTagCompound nbtItem = new NBTTagCompound();
			nbtItem.setTag("ContainedTree", nbtTag);
			stack.setTagCompound(nbtItem);
			return stack;
		}
		return null;
	}
	
	public abstract EnumPileType getPileType();
}
