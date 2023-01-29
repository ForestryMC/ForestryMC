/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.farming.logic;

public enum FarmableReference {

    Arboreal,
    Wheat,
    Cocoa,
    Ender,
    Gourd,
    IC2Crops,
    Infernal,
    Orchard,
    Poales,
    Rubber,
    Shroom,
    Succulentes,
    Vegetables,;

    private final String VALUE;

    public String get() {
        return VALUE;
    }

    FarmableReference() {
        VALUE = "farm" + this.name();
    }
}
