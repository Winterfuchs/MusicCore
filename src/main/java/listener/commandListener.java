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
                String[] roles = properties.getProperties("roles").split(",");
                for (int e = 0; e < roles.length; e++) {
                    if (event.getMember().getRoles().get(i).getName().equals(roles[e])) {
                        commandHandler.handleCommand(commandHandler.parse.parser(event.getMessage().getContentRaw(), event));
                        break;
                    }
                }
            }
            //commandHandler.handleCommand(commandHandler.parse.parser(event.getMessage().getContentRaw(), event));
        }

    }

}
