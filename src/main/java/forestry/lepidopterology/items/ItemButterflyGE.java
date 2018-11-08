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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
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
import forestry.core.utils.EntityUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.lepidopterology.genetics.ButterflyGenome;

public class ItemButterflyGE extends ItemGE implements ISpriteRegister, IColoredItem {

	private static final Random rand = new Random();
	public static final String NBT_AGE = "Age";

	private final EnumFlutterType type;

	public ItemButterflyGE(EnumFlutterType type) {
		super(Tabs.tabLepidopterology);
		this.type = type;
	}

	@Override
	@Nullable
	public IButterfly getIndividual(ItemStack itemstack) {
		return ButterflyManager.butterflyRoot.getMember(itemstack);
	}

	@Override
	protected IAlleleSpecies getSpecies(ItemStack itemStack) {
		return ButterflyGenome.getSpecies(itemStack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			addCreativeItems(subItems, true);
		}
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

					ItemButterflyGE.setAge(butterfly, age);

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

		IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(entityItem.getItem());
		if (butterfly == null) {
			return false;
		}

		if (!butterfly.canTakeFlight(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ)) {
			return false;
		}

		if (entityItem.world.countEntities(EntityButterfly.class) > ModuleLepidopterology.entityConstraint) {
			return false;
		}

		EntityUtil.spawnEntity(entityItem.world,
			new EntityButterfly(entityItem.world, butterfly, entityItem.getPosition()), entityItem.posX,
			entityItem.posY, entityItem.posZ);
		if (!entityItem.getItem().isEmpty()) {
			entityItem.getItem().shrink(1);
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
							ModelBakery.registerItemVariants(this,
								((IAlleleButterflyCocoon) allele).getCocoonItemModel(age));
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
			NBTTagCompound tagCompound = itemstack.getTagCompound();
			IButterflyGenome genome;
			int age;
			if (tagCompound == null) {
				genome = ButterflyDefinition.CabbageWhite.getGenome();
				age = 0;
			} else {
				if (!tagCompound.hasKey(NBT_AGE)) {
					tagCompound.setInteger(NBT_AGE, 0);
				}
				age = tagCompound.getInteger(NBT_AGE);
				IIndividual individual = AlleleManager.alleleRegistry.getIndividual(itemstack);
				Preconditions.checkNotNull(individual);
				genome = (IButterflyGenome) individual.getGenome();
			}
			return genome.getCocoon().getCocoonItemModel(age);
		}

	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
		float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return EnumActionResult.PASS;
		}

		ItemStack stack = player.getHeldItem(hand);

		IButterfly flutter = ButterflyManager.butterflyRoot.getMember(stack);

		IBlockState blockState = world.getBlockState(pos);
		if (type == EnumFlutterType.COCOON) {
			pos = ButterflyManager.butterflyRoot.plantCocoon(world, pos, flutter, player.getGameProfile(), getAge(stack), true);
			if (pos != BlockPos.ORIGIN) {
				PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
				NetworkUtil.sendNetworkPacket(packet, pos, world);

				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				return EnumActionResult.SUCCESS;
			} else {
				return EnumActionResult.PASS;
			}
		} else if (type == EnumFlutterType.CATERPILLAR) {
			IButterflyNursery nursery = GeneticsUtil.getOrCreateNursery(player.getGameProfile(), world, pos, true);
			if (nursery != null) {
				if (!nursery.canNurse(flutter)) {
					return EnumActionResult.PASS;
				}

				nursery.setCaterpillar(flutter);

				PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK,
					PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
				NetworkUtil.sendNetworkPacket(packet, pos, world);

				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
		} else {
			return EnumActionResult.PASS;
		}
	}

	public static void setAge(ItemStack cocoon, int age) {
		if (cocoon.isEmpty()) {
			return;
		}
		if (ButterflyManager.butterflyRoot.getType(cocoon) != EnumFlutterType.COCOON) {
			return;
		}
		NBTTagCompound tagCompound = cocoon.getTagCompound();
		if (tagCompound == null) {
			cocoon.setTagCompound(tagCompound = new NBTTagCompound());
		}
		tagCompound.setInteger(NBT_AGE, age);
	}

	public static int getAge(ItemStack cocoon) {
		if (cocoon.isEmpty()) {
			return 0;
		}
		if (ButterflyManager.butterflyRoot.getType(cocoon) != EnumFlutterType.COCOON) {
			return 0;
		}
		NBTTagCompound tagCompound = cocoon.getTagCompound();
		if (tagCompound == null) {
			return 0;
		}
		return tagCompound.getInteger(NBT_AGE);
	}

	/**
	 * Register butterfly item sprites
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprites(ITextureManager manager) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumButterflyChromosome.SPECIES)) {
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
		String customKey = "for.butterflies.custom." + type.getName() + "."
			+ individual.getGenome().getPrimary().getUnlocalizedName().replace("butterflies.species.", "");
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
