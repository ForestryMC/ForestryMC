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
package forestry.apiculture.genetics;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.api.genetics.IBreedingTracker;
import forestry.apiculture.ModuleApiculture;
import forestry.core.genetics.BreedingTracker;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class ApiaristTracker extends BreedingTracker implements IApiaristTracker {

    /**
     * Required for creation from map storage
     */
    public ApiaristTracker(String s) {
        super(s, ModuleApiculture.beekeepingMode);
    }

    private int queensTotal = 0;
    private int dronesTotal = 0;
    private int princessesTotal = 0;

    @Override
    public void read(CompoundNBT compoundNBT) {

        queensTotal = compoundNBT.getInt("QueensTotal");
        princessesTotal = compoundNBT.getInt("PrincessesTotal");
        dronesTotal = compoundNBT.getInt("DronesTotal");

        super.read(compoundNBT);

    }

    @Override
    public CompoundNBT write(CompoundNBT compoundnbt) {

        compoundnbt.putInt("QueensTotal", queensTotal);
        compoundnbt.putInt("PrincessesTotal", princessesTotal);
        compoundnbt.putInt("DronesTotal", dronesTotal);

        compoundnbt = super.write(compoundnbt);
        return compoundnbt;
    }

    @Override
    public void registerPickup(IIndividual individual) {
        IBeeRoot speciesRoot = (IBeeRoot) individual.getRoot();
        if (!speciesRoot.getUID().equals(speciesRootUID())) {
            return;
        }

        if (!individual.isPureBred(BeeChromosomes.SPECIES)) {
            return;
        }

        IMutationContainer<IBee, ? extends IMutation> container = speciesRoot.getComponent(ComponentKeys.MUTATIONS);
        if (!container.getCombinations(individual.getGenome().getPrimary()).isEmpty()) {
            return;
        }

        registerSpecies(individual.getGenome().getPrimary());
    }

    @Override
    public void registerQueen(IIndividual bee) {
        queensTotal++;
    }

    @Override
    public int getQueenCount() {
        return queensTotal;
    }

    @Override
    public void registerPrincess(IIndividual bee) {
        princessesTotal++;
        registerBirth(bee);
    }

    @Override
    public int getPrincessCount() {
        return princessesTotal;
    }

    @Override
    public void registerDrone(IIndividual bee) {
        dronesTotal++;
        registerBirth(bee);
    }

    @Override
    public int getDroneCount() {
        return dronesTotal;
    }

    @Override
    protected IBreedingTracker getBreedingTracker(PlayerEntity player) {
        //TODO world cast
        return BeeManager.beeRoot.getBreedingTracker(player.world, player.getGameProfile());
    }

    @Override
    protected String speciesRootUID() {
        return BeeRoot.UID;
    }

}
