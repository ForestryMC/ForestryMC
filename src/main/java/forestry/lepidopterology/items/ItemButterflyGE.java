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

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import forestry.api.core.ISpriteRegister;
import forestry.api.core.ISpriteRegistry;
import forestry.api.core.ItemGroups;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.NetworkUtil;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.features.LepidopterologyEntities;
import forestry.lepidopterology.genetics.ButterflyHelper;

import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.utils.AlleleUtils;

import net.minecraft.world.item.Item.Properties;

public class ItemButterflyGE extends ItemGE implements ISpriteRegister, IColoredItem {

	private static final Random rand = new Random();
	public static final String NBT_AGE = "Age";
	public static final int MAX_AGE = 3;
	//private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	private final EnumFlutterType type;

	public ItemButterflyGE(EnumFlutterType type) {
		super(new Properties().tab(ItemGroups.tabLepidopterology));
		this.type = type;
		/*ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)attackSpeedIn, AttributeModifier.Operation.ADDITION));
		if (type == EnumFlutterType.COCOON) {
			addPropertyOverride(new ResourceLocation("age"), (stack, world, livingEntity) -> getAge(stack));
		}
		this.attributeModifiers = builder.build();*/
	}

	@Override
	protected IAlleleForestrySpecies getSpecies(ItemStack itemStack) {
		return GeneticHelper.getOrganism(itemStack).getAllele(ButterflyChromosomes.SPECIES, true);
	}

	@Override
	protected IOrganismType getType() {
		return type;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return GeneticHelper.createOrganism(stack, type, ButterflyHelper.getRoot().getDefinition());
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
		if (this.allowedIn(tab)) {
			addCreativeItems(subItems, true);
		}
	}

	public void addCreativeItems(NonNullList<ItemStack> subItems, boolean hideSecrets) {
		if (type == EnumFlutterType.COCOON) {
			for (int age = 0; age < MAX_AGE; age++) {
				for (IButterfly individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
					// Don't show secret butterflies unless ordered to.
					if (hideSecrets && individual.isSecret() && !Config.isDebug) {
						continue;
					}

					ItemStack butterfly = ButterflyManager.butterflyRoot.getTypes().createStack(individual, type);

					ItemButterflyGE.setAge(butterfly, age);

					subItems.add(butterfly);
				}
			}
		} else {
			for (IButterfly individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
				// Don't show secret butterflies unless ordered to.
				if (hideSecrets && individual.isSecret() && !Config.isDebug) {
					continue;
				}

				subItems.add(ButterflyManager.butterflyRoot.getTypes().createStack(individual, type));
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem) {
		if (type != EnumFlutterType.BUTTERFLY) {
			return false;
		}
		if (entityItem.level.isClientSide || entityItem.tickCount < 80) {
			return false;
		}
		if (rand.nextInt(24) != 0) {
			return false;
		}

		IButterfly butterfly = ButterflyManager.butterflyRoot.getTypes().createIndividual(entityItem.getItem()).orElse(null);
		if (butterfly == null) {
			return false;
		}

		if (butterfly.canTakeFlight(entityItem.level, entityItem.getX(), entityItem.getY(), entityItem.getZ())) {
			return false;
		}

		if (false) {//TODO entityItem.world.countEntities(EntityButterfly.class) > ModuleLepidopterology.entityConstraint) {
			return false;
		}

		EntityUtil.spawnEntity(entityItem.level,
				EntityButterfly.create(LepidopterologyEntities.BUTTERFLY.entityType(), entityItem.level, butterfly, entityItem.blockPosition()), entityItem.getX(), entityItem.getY(), entityItem.getZ());
		if (!entityItem.getItem().isEmpty()) {
			entityItem.getItem().shrink(1);
		} else {
			entityItem.remove(Entity.RemovalReason.DISCARDED);
		}
		return true;
	}

	/* MODELS */
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public void registerModel(Item item, IModelManager manager) {
	//		switch (this.type) {
	//			case CATERPILLAR:
	//				manager.registerItemModel(item, 0, "caterpillar");
	//				break;
	//			case BUTTERFLY:
	//				manager.registerItemModel(item, 0, "butterflyge");
	//				break;
	//			case COCOON:
	//				manager.registerItemModel(item, new CocoonMeshDefinition());
	//				for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
	//					if (allele instanceof IAlleleButterflyCocoon) {
	//						for (int age = 0; age < 3; age++) {
	//							ModelBakery.registerItemVariants(this,
	//								((IAlleleButterflyCocoon) allele).getCocoonItemModel(age));
	//						}
	//					}
	//				}
	//				break;
	//			default:
	//				manager.registerItemModel(item, 0, "liquids/jar");
	//		}
	//	}

	//	private static class CocoonMeshDefinition implements ItemMeshDefinition {
	//		@Override
	//		public ModelResourceLocation getModelLocation(ItemStack itemstack) {
	//			CompoundNBT tagCompound = itemstack.getTag();
	//			IButterflyGenome genome;
	//			int age;
	//			if (tagCompound == null) {
	//				genome = ButterflyDefinition.CabbageWhite.getGenome();
	//				age = 0;
	//			} else {
	//				if (!tagCompound.contains(NBT_AGE)) {
	//					tagCompound.putInt(NBT_AGE, 0);
	//				}
	//				age = tagCompound.getInt(NBT_AGE);
	//				IIndividual individual = AlleleManager.alleleRegistry.getIndividual(itemstack);
	//				Preconditions.checkNotNull(individual);
	//				genome = (IButterflyGenome) individual.getGenome();
	//			}
	//			return genome.getCocoon().getCocoonItemModel(age);
	//		}
	//
	//	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		if (world.isClientSide) {
			return InteractionResult.PASS;
		}

		ItemStack stack = player.getItemInHand(context.getHand());

		IButterfly flutter = ButterflyManager.butterflyRoot.getTypes().createIndividual(stack).orElse(null);

		BlockState blockState = world.getBlockState(pos);
		if (type == EnumFlutterType.COCOON) {
			pos = ButterflyManager.butterflyRoot.plantCocoon(world, pos, flutter, player.getGameProfile(), getAge(stack), true);
			if (pos != BlockPos.ZERO) {
				PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
				NetworkUtil.sendNetworkPacket(packet, pos, world);

				if (!player.isCreative()) {
					stack.shrink(1);
				}
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.PASS;
			}
		} else if (type == EnumFlutterType.CATERPILLAR) {
			IButterflyNursery nursery = GeneticsUtil.getOrCreateNursery(player.getGameProfile(), world, pos, true);
			if (nursery != null) {
				if (!nursery.canNurse(flutter)) {
					return InteractionResult.PASS;
				}

				nursery.setCaterpillar(flutter);

				PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK,
					PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
				NetworkUtil.sendNetworkPacket(packet, pos, world);

				if (!player.isCreative()) {
					stack.shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else {
			return InteractionResult.PASS;
		}
	}

	public static void setAge(ItemStack cocoon, int age) {
		if (cocoon.isEmpty()) {
			return;
		}
		if (ButterflyManager.butterflyRoot.getTypes().getType(cocoon).orElse(null) != EnumFlutterType.COCOON) {
			return;
		}
		CompoundTag tagCompound = cocoon.getTag();
		if (tagCompound == null) {
			cocoon.setTag(tagCompound = new CompoundTag());
		}
		tagCompound.putInt(NBT_AGE, age);
	}

	public static int getAge(ItemStack cocoon) {
		if (cocoon.isEmpty()) {
			return 0;
		}
		if (ButterflyManager.butterflyRoot.getTypes().getType(cocoon).orElse(null) != EnumFlutterType.COCOON) {
			return 0;
		}
		CompoundTag tagCompound = cocoon.getTag();
		if (tagCompound == null) {
			return 0;
		}
		return tagCompound.getInt(NBT_AGE);
	}

	/**
	 * Register butterfly item sprites
	 *
	 * @param registry
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerSprites(ISpriteRegistry registry) {
		AlleleUtils.forEach(ButterflyChromosomes.SPECIES, (allele) -> allele.registerSprites(registry));
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int tintIndex) {
		if (stack.hasTag()) {
			IIndividual individual = GeneticHelper.getIndividual(stack).orElse(null);
			if (individual != null) {
				IAlleleSpecies species = individual.getGenome().getPrimary();
				return ((IAlleleForestrySpecies) species).getSpriteColour(tintIndex);
			}
		}
		return 0xffffff;
	}

}
