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
package forestry.apiculture.commands;

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.core.commands.IStatsSaveHelper;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;
import genetics.utils.AlleleUtils;
import net.minecraft.world.World;

import java.util.Collection;

public class BeeStatsSaveHelper implements IStatsSaveHelper {

    @Override
    public String getUnlocalizedSaveStatsString() {
        return "for.chat.command.forestry.bee.save.stats";
    }

    @Override
    public void addExtraInfo(Collection<String> statistics, IBreedingTracker breedingTracker) {
        IApiaristTracker tracker = (IApiaristTracker) breedingTracker;
        String discoveredLine = Translator.translateToLocal("for.chat.command.forestry.stats.save.key.discovered") + ":";
        statistics.add(discoveredLine);
        statistics.add(StringUtil.line(discoveredLine.length()));

        String queen = Translator.translateToLocal("for.bees.grammar.queen.type");
        String princess = Translator.translateToLocal("for.bees.grammar.princess.type");
        String drone = Translator.translateToLocal("for.bees.grammar.drone.type");
        statistics.add(queen + ":\t\t" + tracker.getQueenCount());
        statistics.add(princess + ":\t" + tracker.getPrincessCount());
        statistics.add(drone + ":\t\t" + tracker.getDroneCount());
        statistics.add("");
    }

    @Override
    public Collection<IAlleleBeeSpecies> getSpecies() {
        return AlleleUtils.filteredAlleles(BeeChromosomes.SPECIES);
    }

    @Override
    public String getFileSuffix() {
        return "bees";
    }

    @Override
    public IBreedingTracker getBreedingTracker(World world, GameProfile gameProfile) {
        //TODO world cast
        return BeeManager.beeRoot.getBreedingTracker(world, gameProfile);
    }

}
