package forestry.core.genetics.root;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;

public interface IResearchPlugin {
	float getResearchSuitability(IAlleleSpecies species, ItemStack itemstack);

	default NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		return NonNullList.create();
	}
}
