package forestry.mail;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.api.mail.IPostalState;

public class ResponseNotMailable implements IPostalState {
    private final IPostalState state;

    public ResponseNotMailable(IPostalState state) {
        this.state = state;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("for.chat.mail.response.not.mailable.format", state.getDescription());
    }
}
