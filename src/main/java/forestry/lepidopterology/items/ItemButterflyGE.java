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
package forestry.lepidopterology.items;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.ITree;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.ButterflyGenome;

public class ItemButterflyGE extends ItemGE implements ISpriteRegister {

	private static final Random rand = new Random();
	public static final String NBT_AGE = "Age";

	private final EnumFlutterType type;

	public ItemButterflyGE(EnumFlutterType type) {
		super(Tabs.tabLepidopterology);
		this.type = type;
	}

	@Override
	public IButterfly getIndividual(ItemStack itemstack) {
		return ButterflyManager.butterflyRoot.getMember(itemstack);
	}

	@Override
	protected IAlleleSpecies getSpecies(ItemStack itemStack) {
		return ButterflyGenome.getSpecies(itemStack);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		if (itemstack.getTagCompound() == null) {
			return "???";
		}

		IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(itemstack);
		if (butterfly == null) {
			return "???";
		}

		return butterfly.getDisplayName();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		addCreativeItems(itemList, true);
	}

	public void addCreativeItems(List<ItemStack> itemList, boolean hideSecrets) {
		if(type == EnumFlutterType.COCOON){
			for(int age = 0;age < 3;age++){
				for (IIndividual individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
					// Don't show secret butterflies unless ordered to.
					if (hideSecrets && individual.isSecret() && !Config.isDebug) {
						continue;
					}
		
					ItemStack butterfly = ButterflyManager.butterflyRoot.getMemberStack(individual, type);
					
					if(type == EnumFlutterType.COCOON){
						butterfly.getTagCompound().setInteger(NBT_AGE, age);
					}
					
					itemList.add(butterfly);
				}
			}
		}else{
			for (IIndividual individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
				// Don't show secret butterflies unless ordered to.
				if (hideSecrets && individual.isSecret() && !Config.isDebug) {
					continue;
				}
				
				itemList.add(ButterflyManager.butterflyRoot.getMemberStack(individual, type));
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (type != EnumFlutterType.BUTTERFLY) {
			return false;
		}
		if (entityItem.worldObj.isRemote || entityItem.ticksExisted < 80) {
			return false;
		}
		if (rand.nextInt(24) != 0) {
			return false;
		}

		IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(entityItem.getEntityItem());
		if (butterfly == null) {
			return false;
		}

		if (!butterfly.canTakeFlight(entityItem.worldObj, entityItem.posX, entityItem.posY, entityItem.posZ)) {
			return false;
		}

		if (entityItem.worldObj.countEntities(EntityButterfly.class) > PluginLepidopterology.entityConstraint) {
			return false;
		}

		if (EntityUtil.spawnEntity(entityItem.worldObj, new EntityButterfly(entityItem.worldObj, butterfly), entityItem.posX, entityItem.posY, entityItem.posZ) != null) {
			if (entityItem.getEntityItem().stackSize > 1) {
				entityItem.getEntityItem().stackSize--;
			} else {
				entityItem.setDead();
			}
			return true;
		}

		return false;
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound()) {
			return super.getColorFromItemStack(itemstack, renderPass);
		}

		return getColourFromSpecies(AlleleManager.alleleRegistry.getIndividual(itemstack).getGenome().getPrimary(), renderPass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {
		if (species != null) {
			return species.getSpriteColour(renderPass);
		} else {
			return 0xffffff;
		}

	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		switch (this.type) {
			case CATERPILLAR:
				manager.registerItemModel(item, 0, "caterpillar");
				break;
			case BUTTERFLY:
				manager.registerItemModel(item, 0, "butterflyGE");
				break;
			case COCOON:
				manager.registerItemModel(item, new CocoonMeshDefinition());
				for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
					if (allele instanceof IAlleleButterflyCocoon) {
						for(int age = 0;age < 3;age++){
							manager.registerVariant(this, ((IAlleleButterflyCocoon) allele).getCocoonItemModel(age));
						}
					}
				}
				break;
			default:
				manager.registerItemModel(item, 0, "liquids/jar");
		}
	}
	
	private static class CocoonMeshDefinition implements ItemMeshDefinition {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack itemstack) {
			int age = itemstack.getTagCompound().getInteger(NBT_AGE);
			IButterflyGenome genome = (IButterflyGenome) AlleleManager.alleleRegistry.getIndividual(itemstack).getGenome();
			return genome.getCocoon().getCocoonItemModel(age);
		}
		
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

		if (world.isRemote) {
			return false;
		}

		IButterfly flutter = ButterflyManager.butterflyRoot.getMember(stack);
		if (flutter == null) {
			return false;
		}

		if (type == EnumFlutterType.COCOON) {
			int age = stack.getTagCompound().getInteger(NBT_AGE);
			
			// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
			int yShift;
			if (!BlockUtil.isReplaceableBlock(world, pos)) {
				if(!world.isAirBlock(pos.add(0, -1, 0))){
					return false;
				}
				yShift = 1;
			} else {
				yShift = 0;
			}
			BlockPos posS = pos.add(0, -yShift, 0);
			
			IButterflyNursery nursery = null;
			
			if(world.getTileEntity(pos) instanceof IButterflyNursery){
				nursery = (IButterflyNursery) world.getTileEntity(pos);
			}else{
				ITree treeLeave = GeneticsUtil.getErsatzPollen(world, pos);
				
				if(treeLeave != null){
					treeLeave.setLeaves(world, player.getGameProfile(), pos);
					nursery = (IButterflyNursery) world.getTileEntity(pos);
				}
			}
			if(nursery != null){
				if(nursery.canNurse(flutter)){
					nursery.setCaterpillar(flutter);
					if(ButterflyManager.butterflyRoot.plantCocoon(world, nursery, player.getGameProfile(), age)){
						Proxies.common.addBlockPlaceEffects(world, pos, world.getBlockState(posS));
						if (!player.capabilities.isCreativeMode) {
							stack.stackSize--;
						}
						return true;
					}else{
						nursery.setCaterpillar(null);
						return false;
					}
				}
			}
			return false;
		}else if (type == EnumFlutterType.CATERPILLAR) {

			TileEntity target = world.getTileEntity(pos);
			if (!(target instanceof IButterflyNursery)) {
				return false;
			}

			IButterflyNursery pollinatable = (IButterflyNursery) target;
			if (!pollinatable.canNurse(flutter)) {
				return false;
			}

			pollinatable.setCaterpillar(flutter);
			Proxies.common.sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.LEAF, world, pos,
					world.getBlockState(pos));
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
			}
			return true;

		} else {
			return false;
		}
	}

	/**
	 * Register butterfly item sprites
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprites(ITextureManager manager) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleButterflySpecies) {
				((IAlleleButterflySpecies) allele).getSpriteProvider().registerSprites();
			}
		}
	}

}
