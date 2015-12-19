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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EntityUtil;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.ButterflyGenome;
import forestry.plugins.PluginLepidopterology;

public class ItemButterflyGE extends ItemGE {

	private static final Random rand = new Random();

	private final EnumFlutterType type;

	public ItemButterflyGE(EnumFlutterType type) {
		super(Tabs.tabLepidopterology);
		this.type = type;
	}

	@Override
	protected int getDefaultPrimaryColour() {
		return 0;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0;
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		addCreativeItems(itemList, true);
	}

	public void addCreativeItems(List<ItemStack> itemList, boolean hideSecrets) {

		for (IIndividual individual : ButterflyManager.butterflyRoot.getIndividualTemplates()) {
			// Don't show secret butterflies unless ordered to.
			if (hideSecrets && individual.isSecret() && !Config.isDebug) {
				continue;
			}

			itemList.add(ButterflyManager.butterflyRoot.getMemberStack(individual, type.ordinal()));
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

	/* ICONS FOR SERUMS */
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound()) {
			return super.getColorFromItemStack(itemstack, renderPass);
		}

		return getColourFromSpecies(AlleleManager.alleleRegistry.getIndividual(itemstack).getGenome().getPrimary(), renderPass);
	}

	@Override
	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {
		if (species != null) {
			return species.getSpriteColour(renderPass);
		} else {
			return 0xffffff;
		}

	}

	/* MODELS */
	@Override
	public void registerModel(Item item, IModelManager manager) {
		switch (this.type) {
		case CATERPILLAR:
			manager.registerItemModel(item, 0, "caterpillar.body");
			break;
		default:
			manager.registerItemModel(item, 0, "liquids/jar");
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

		if (type == EnumFlutterType.CATERPILLAR) {

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

}
