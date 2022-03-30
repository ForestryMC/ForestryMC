package forestry.api.genetics;

import java.util.Map;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

import forestry.core.genetics.root.IResearchPlugin;

public interface IResearchHandler<I extends IIndividual> extends IRootComponent<I> {

	/**
	 * Sets an item stack as a valid (generic) research catalyst for this class.
	 *
	 * @param stack       ItemStack to set as suitable.
	 * @param suitability Float between 0 and 1 to indicate suitability.
	 */
	void setResearchSuitability(ItemStack stack, float suitability);

	void addPlugin(IResearchPlugin plugin);

	/**
	 * @return List of generic catalysts which should be accepted for research by species of this class.
	 */
	Map<ItemStack, Float> getResearchCatalysts();

	/**
	 * @return A float signifying the chance for the passed itemstack to yield a research success.
	 */
	float getResearchSuitability(IAlleleSpecies species, ItemStack itemstack);

	/**
	 * @return ItemStacks representing the bounty for this research success.
	 */
	NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, Level world, GameProfile gameProfile, I individual, int bountyLevel);

	@Override
	ComponentKey<IResearchHandler> getKey();
}
