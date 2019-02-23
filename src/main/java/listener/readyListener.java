package listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class readyListener extends ListenerAdapter {

    public void onReady(ReadyEvent event) {
        String out = "\nThis bot is running on following Servers: \n";

        for (Guild g : event.getJDA().getGuilds()) {
            out += g.getName() + " (" + g.getId() + ") \n";
        }

        System.out.println(out);
    }
}
