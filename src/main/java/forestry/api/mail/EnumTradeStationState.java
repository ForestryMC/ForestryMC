/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.mail;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum EnumTradeStationState implements IPostalState {
    OK("for.chat.mail.ok"),
    INSUFFICIENT_OFFER("for.chat.mail.insufficient.offer"),
    INSUFFICIENT_TRADE_GOOD("for.chat.mail.insufficient.trade.good"),
    INSUFFICIENT_BUFFER("for.chat.mail.insufficient.buffer"),
    INSUFFICIENT_PAPER("for.chat.mail.insufficient.paper"),
    INSUFFICIENT_STAMPS("for.chat.mail.insufficient.stamps");

    private final String unlocalizedDescription;

    EnumTradeStationState(String unlocalizedDescription) {
        this.unlocalizedDescription = unlocalizedDescription;
    }

    @Override
    public boolean isOk() {
        return this == OK;
    }

    //TODO - make sure this is not called on the server (or perhaps the method needs to
    //return a translationtextcomponent
    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent(unlocalizedDescription);
    }
}
