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
package forestry.lepidopterology.blocks;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflyCocoon;
import forestry.core.blocks.properties.PropertyAllele;
import genetics.utils.AlleleUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PropertyCocoon extends PropertyAllele<IAlleleButterflyCocoon> {
    private static final Map<String, IAlleleButterflyCocoon> namesMap = new HashMap<>();

    public PropertyCocoon(String name) {
        super(name, IAlleleButterflyCocoon.class);
    }

    @Override
    public Class<IAlleleButterflyCocoon> getValueClass() {
        return IAlleleButterflyCocoon.class;
    }

    @Override
    public Collection<IAlleleButterflyCocoon> getAllowedValues() {
        return AlleleUtils.filteredAlleles(ButterflyChromosomes.COCOON);
    }

    @Override
    public String getName(IAlleleButterflyCocoon value) {
        return value.getCocoonName();
    }

    @Override
    public Optional<IAlleleButterflyCocoon> parseValue(String value) {
        if (namesMap.isEmpty()) {
            // Using the stream here so we can save one 'collect' call in 'getRegisteredAlleles()'
            AlleleUtils.filteredStream(ButterflyChromosomes.COCOON).forEach(cocoon -> {
                String propertyName = getName(cocoon);
                namesMap.put(propertyName, cocoon);
            });
        }
        return Optional.ofNullable(namesMap.get(value));
    }
}
