package listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import properties.AppProperties;
import util.STATIC;
import core.*;

public class commandListener extends ListenerAdapter {

    AppProperties properties;

    public void onMessageReceived(MessageReceivedEvent event) {
        properties = new AppProperties();
        if (event.getMessage().getContentRaw().startsWith(STATIC.PREFIX) && event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {
            for (int i = 0; i < event.getMember().getRoles().size(); i++) {
                if (properties.hasPermission(event.getMember())) {
                    commandHandler.handleCommand(commandHandler.parse.parser(event.getMessage().getContentRaw(), event));
                    break;
                }
            }
        }
        //commandHandler.handleCommand(commandHandler.parse.parser(event.getMessage().getContentRaw(), event));
    }

}
