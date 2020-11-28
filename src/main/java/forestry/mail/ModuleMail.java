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
package forestry.mail;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.PostManager;
import forestry.api.modules.ForestryModule;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.mail.commands.CommandMail;
import forestry.mail.features.MailContainers;
import forestry.mail.gui.*;
import forestry.mail.network.PacketRegistryMail;
import forestry.mail.triggers.MailTriggers;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.MAIL, name = "Mail", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.mail.description")
public class ModuleMail extends BlankForestryModule {

    @Override
    public void setupAPI() {
        PostManager.postRegistry = new PostRegistry();
        PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
        PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(MailContainers.CATALOGUE.containerType(), GuiCatalogue::new);
        ScreenManager.registerFactory(MailContainers.LETTER.containerType(), GuiLetter::new);
        ScreenManager.registerFactory(MailContainers.MAILBOX.containerType(), GuiMailbox::new);
        ScreenManager.registerFactory(MailContainers.STAMP_COLLECTOR.containerType(), GuiStampCollector::new);
        ScreenManager.registerFactory(MailContainers.TRADE_NAME.containerType(), GuiTradeName::new);
        ScreenManager.registerFactory(MailContainers.TRADER.containerType(), GuiTrader::new);
    }

    @Override
    public void preInit() {
        ModuleCore.rootCommand.then(CommandMail.register());

        if (Config.mailAlertEnabled) {
            MinecraftForge.EVENT_BUS.register(new EventHandlerMailAlert());
        }
    }

    @Override
    public void registerTriggers() {
        MailTriggers.initialize();
    }

    @Override
    public IPacketRegistry getPacketRegistry() {
        return new PacketRegistryMail();
    }

    @Override
    public ISaveEventHandler getSaveEventHandler() {
        return new SaveEventHandlerMail();
    }
}
