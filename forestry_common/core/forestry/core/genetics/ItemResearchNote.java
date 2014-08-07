/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.genetics;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.items.ItemForestry;
import forestry.core.proxy.Proxies;

public class ItemResearchNote extends ItemForestry {

	public static enum EnumNoteType {
		NONE, MUTATION, SPECIES;

		public static EnumNoteType[] VALUES = values();

		private IMutation getEncodedMutation(ISpeciesRoot root, NBTTagCompound compound) {
			IAllele allele0 = AlleleManager.alleleRegistry.getAllele(compound.getString("AL0"));
			IAllele allele1 = AlleleManager.alleleRegistry.getAllele(compound.getString("AL1"));
			if(allele0 == null || allele1 == null)
				return null;

			IAllele result = null;
			if(compound.hasKey("RST"))
				result = AlleleManager.alleleRegistry.getAllele(compound.getString("RST"));

			IMutation encoded = null;
			for(IMutation mutation : root.getCombinations(allele0))
				if(mutation.isPartner(allele1)) {
					if(result == null
							|| mutation.getTemplate()[0].getUID().equals(result.getUID())) {
						encoded = mutation;
						break;
					}
				}

			return encoded;
		}

		public ArrayList<String> getTooltip(NBTTagCompound compound) {
			ArrayList<String> tooltips = new ArrayList<String>();

			if(compound == null || this == NONE)
				return tooltips;

			if(this == MUTATION) {
				ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(compound.getString("ROT"));
				if(root == null)
					return tooltips;

				IMutation encoded = getEncodedMutation(root, compound);
				if(encoded == null)
					return tooltips;

				tooltips.add("\u00A7aMutation discovered!");
				tooltips.add(String.format("\u00A7e'%s'\u00A77 and \u00A7e'%s'\u00A77.", encoded.getAllele0().getName(), encoded.getAllele1().getName()));
				tooltips.add(String.format("Chance for success is \u00A79%s\u00A77.", EnumMutateChance.rateChance(encoded.getBaseChance())));
				tooltips.add(String.format("New species is \u00A7d'%s'\u00A77.", (encoded.getTemplate()[root.getKaryotypeKey().ordinal()].getName())));

				if(encoded.getSpecialConditions() != null && encoded.getSpecialConditions().size() > 0) {
					for(String line : encoded.getSpecialConditions())
						tooltips.add("\u00A76" + line);
				}
			} else if(this == SPECIES) {
				IAlleleSpecies allele0 = (IAlleleSpecies)AlleleManager.alleleRegistry.getAllele(compound.getString("AL0"));
				if(allele0 == null)
					return tooltips;
				ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(compound.getString("ROT"));
				if(root == null)
					return tooltips;

				tooltips.add("\u00A7aSpecies researched!");
				tooltips.add(String.format("\u00A7e'%s'\u00A77 (%s)", allele0.getName(), allele0.getBinomial()));
			}

			return tooltips;
		}

		public boolean registerResults(World world, EntityPlayer player, NBTTagCompound compound) {
			if(compound == null || this == NONE)
				return false;

			if(this == MUTATION) {
				ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(compound.getString("ROT"));
				if(root == null)
					return false;

				IMutation encoded = getEncodedMutation(root, compound);
				if(encoded == null)
					return false;

				IBreedingTracker tracker = encoded.getRoot().getBreedingTracker(world, player.getGameProfile());
				if(tracker.isDiscovered(encoded)) {
					player.addChatMessage(new ChatComponentTranslation("chat.cannotmemorizeagain"));
					return false;
				}

				tracker.registerSpecies((IAlleleSpecies)encoded.getAllele0());
				tracker.registerSpecies((IAlleleSpecies)encoded.getAllele1());
				tracker.registerSpecies((IAlleleSpecies)encoded.getTemplate()[root.getKaryotypeKey().ordinal()]);
				tracker.registerMutation(encoded);
				player.addChatMessage(new ChatComponentTranslation("chat.memorizednote"));
				return true;
			}

			return false;

		}

		public static ResearchNote createMutationNote(GameProfile researcher, IMutation mutation) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("ROT", mutation.getRoot().getUID());
			compound.setString("AL0", mutation.getAllele0().getUID());
			compound.setString("AL1", mutation.getAllele1().getUID());
			compound.setString("RST", mutation.getTemplate()[0].getUID());
			return new ResearchNote(researcher, MUTATION, compound);
		}

		public static ItemStack createMutationNoteStack(Item item, GameProfile researcher, IMutation mutation) {
			ResearchNote note = createMutationNote(researcher, mutation);
			NBTTagCompound compound = new NBTTagCompound();
			note.writeToNBT(compound);
			ItemStack created = new ItemStack(item);
			created.setTagCompound(compound);
			return created;
		}

		public static ResearchNote createSpeciesNote(GameProfile researcher, IAlleleSpecies species) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("ROT", species.getRoot().getUID());
			compound.setString("AL0", species.getUID());
			return new ResearchNote(researcher, SPECIES, compound);
		}

		public static ItemStack createSpeciesNoteStack(Item item, GameProfile researcher, IAlleleSpecies species) {
			ResearchNote note = createSpeciesNote(researcher, species);
			NBTTagCompound compound = new NBTTagCompound();
			note.writeToNBT(compound);
			ItemStack created = new ItemStack(item);
			created.setTagCompound(compound);
			return created;
		}

	}

	public static class ResearchNote {

		private GameProfile researcher;
		private EnumNoteType type = EnumNoteType.NONE;
		private NBTTagCompound inner;

		public ResearchNote(GameProfile researcher, EnumNoteType type, NBTTagCompound inner) {
			this.researcher = researcher;
			this.type = type;
			this.inner = inner;
		}

		public ResearchNote(NBTTagCompound compound) {
			if(compound != null) {
				if (compound.hasKey("res")) {
					researcher = NBTUtil.func_152459_a(compound.getCompoundTag("res"));
				}
				this.type = EnumNoteType.VALUES[compound.getByte("TYP")];
				this.inner = compound.getCompoundTag("INN");
			} else
				this.inner = new NBTTagCompound();
		}

		public void writeToNBT(NBTTagCompound compound) {
			if (this.researcher != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				NBTUtil.func_152460_a(nbt, researcher);
				compound.setTag("res", nbt);
			}
			compound.setByte("TYP", (byte)type.ordinal());
			compound.setTag("INN", inner);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void addTooltip(List list) {
			ArrayList<String> tooltips = type.getTooltip(inner);
			if(tooltips.size() <= 0) {
				list.add("\u00A7o\u00A7cIt is impossible to decipher the washed out scribbles.");
				list.add("The note has become unreadable and is worthless.");
				return;
			}

			list.addAll(tooltips);
		}

		public boolean registerResults(World world, EntityPlayer player) {
			return type.registerResults(world, player, inner);
		}
	}

	public ItemResearchNote() {
		super();
		setCreativeTab(null);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		ResearchNote note = new ResearchNote(itemstack.getTagCompound());
		return String.format("%s's Notes", note.researcher);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		ResearchNote note = new ResearchNote(itemstack.getTagCompound());
		note.addTooltip(list);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(world))
			return itemstack;

		ResearchNote note = new ResearchNote(itemstack.getTagCompound());
		if(note.registerResults(world, entityplayer)) {
			entityplayer.inventory.decrStackSize(entityplayer.inventory.currentItem, 1);
			// Notify player that his inventory has changed.
			Proxies.net.inventoryChangeNotify(entityplayer);
		}

		return itemstack;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		return 0xffe8a5;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		return Items.paper.getIconFromDamage(0);
	}
}
