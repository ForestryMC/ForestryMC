package forestry.core.genetics.root;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;

public interface IResearchPlugin {
	float getResearchSuitability(IAlleleSpecies species, ItemStack itemstack);

	default NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, Level world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		return NonNullList.create();
	}
}
