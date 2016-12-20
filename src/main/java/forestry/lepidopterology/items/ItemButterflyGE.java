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

import java.util.Random;

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
import forestry.core.items.IColoredItem;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Translator;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.ButterflyGenome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemButterflyGE extends ItemGE implements ISpriteRegister, IColoredItem {

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
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		addCreativeItems(subItems, true);
	}

	public void addCreativeItems(NonNullList<ItemStack> subItems, boolean hideSecrets) {
		if (type == EnumFlutterType.COCOON) {
			for (int age = 0; age < 3; age++) {
				for (IIndividual individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
					// Don't show secret butterflies unless ordered to.
					if (hideSecrets && individual.isSecret() && !Config.isDebug) {
						continue;
					}

					ItemStack butterfly = ButterflyManager.butterflyRoot.getMemberStack(individual, type);

					if (butterfly.getTagCompound() != null) {
						butterfly.getTagCompound().setInteger(NBT_AGE, age);
					}

					subItems.add(butterfly);
				}
			}
		} else {
			for (IIndividual individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
				// Don't show secret butterflies unless ordered to.
				if (hideSecrets && individual.isSecret() && !Config.isDebug) {
					continue;
				}

				subItems.add(ButterflyManager.butterflyRoot.getMemberStack(individual, type));
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (type != EnumFlutterType.BUTTERFLY) {
			return false;
		}
		if (entityItem.world.isRemote || entityItem.ticksExisted < 80) {
			return false;
		}
		if (rand.nextInt(24) != 0) {
			return false;
		}

		IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(entityItem.getEntityItem());

		if (!butterfly.canTakeFlight(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ)) {
			return false;
		}

		if (entityItem.world.countEntities(EntityButterfly.class) > PluginLepidopterology.entityConstraint) {
			return false;
		}

		EntityUtil.spawnEntity(entityItem.world, new EntityButterfly(entityItem.world, butterfly, entityItem.getPosition()), entityItem.posX, entityItem.posY, entityItem.posZ);
		if (!entityItem.getEntityItem().isEmpty()) {
			entityItem.getEntityItem().shrink(1);
		} else {
			entityItem.setDead();
		}
		return true;
	}

	/* MODELS */
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
						for (int age = 0; age < 3; age++) {
							ModelBakery.registerItemVariants(this, ((IAlleleButterflyCocoon) allele).getCocoonItemModel(age));
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
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return EnumActionResult.PASS;
		}

		ItemStack stack = player.getHeldItem(hand);

		IButterfly flutter = ButterflyManager.butterflyRoot.getMember(stack);

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		IBlockState blockState = worldIn.getBlockState(pos);
		if (type == EnumFlutterType.COCOON) {
			int age = stack.getTagCompound().getInteger(NBT_AGE);

			// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
			int yShift;
			if (!BlockUtil.isReplaceableBlock(blockState, worldIn, pos)) {
				if (!worldIn.isAirBlock(pos.down())) {
					return EnumActionResult.PASS;
				}
				yShift = 1;
			} else {
				yShift = 0;
			}
			BlockPos posS = pos.add(0, -yShift, 0);

			IButterflyNursery nursery = null;

			if (tileEntity instanceof IButterflyNursery) {
				nursery = (IButterflyNursery) tileEntity;
			} else {
				IIndividual treeLeave = GeneticsUtil.getPollen(worldIn, pos);

				if (treeLeave != null && treeLeave instanceof ITree) {
					if (((ITree) treeLeave).setLeaves(worldIn, player.getGameProfile(), pos)) {
						nursery = (IButterflyNursery) worldIn.getTileEntity(pos);
					}
				}
			}
			if (nursery != null) {
				if (nursery.canNurse(flutter)) {
					nursery.setCaterpillar(flutter);
					if (ButterflyManager.butterflyRoot.plantCocoon(worldIn, nursery, player.getGameProfile(), age)) {
						PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, worldIn.getBlockState(posS));
						Proxies.net.sendNetworkPacket(packet, pos, worldIn);

						if (!player.capabilities.isCreativeMode) {
							stack.shrink(1);
						}
						nursery.setCaterpillar(null);
						return EnumActionResult.SUCCESS;
					} else {
						nursery.setCaterpillar(null);
						return EnumActionResult.PASS;
					}
				}
			}
			return EnumActionResult.PASS;
		} else if (type == EnumFlutterType.CATERPILLAR) {

			if (!(tileEntity instanceof IButterflyNursery)) {
				return EnumActionResult.PASS;
			}

			IButterflyNursery pollinatable = (IButterflyNursery) tileEntity;
			if (!pollinatable.canNurse(flutter)) {
				return EnumActionResult.PASS;
			}

			pollinatable.setCaterpillar(flutter);

			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
			Proxies.net.sendNetworkPacket(packet, pos, worldIn);

			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			return EnumActionResult.SUCCESS;

		} else {
			return EnumActionResult.PASS;
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
				((IAlleleButterflySpecies) allele).registerSprites();
			}
		}
	}


	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.getTagCompound() == null) {
			return super.getItemStackDisplayName(itemstack);
		}

		IButterfly individual = ButterflyManager.butterflyRoot.getMember(itemstack);
		String customKey = "for.butterflies.custom." + type.getName() + "." + individual.getGenome().getPrimary().getUnlocalizedName().replace("butterflies.species.", "");
		if (Translator.canTranslateToLocal(customKey)) {
			return Translator.translateToLocal(customKey);
		}
		String grammar = Translator.translateToLocal("for.butterflies.grammar." + type.getName());
		String speciesString = individual.getDisplayName();
		String typeString = Translator.translateToLocal("for.butterflies.grammar." + type.getName() + ".type");
		return grammar.replaceAll("%SPECIES", speciesString).replaceAll("%TYPE", typeString);
	}

	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		if (stack.getTagCompound() != null) {
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(stack);
			if (individual != null) {
				IAlleleSpecies species = individual.getGenome().getPrimary();
				return species.getSpriteColour(tintIndex);
			}
		}
		return 0xffffff;
	}

}
