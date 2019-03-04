package properties;

import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    public void init() {
        Properties prop = new Properties();
        OutputStream output = null;
        File f = new File("config.properties");
        try {
            if (!f.exists() && !f.isDirectory()) {
                output = new FileOutputStream("config.properties");

                // set token here
                prop.setProperty("token", "your_token");
                prop.setProperty("roles", "role1,role2");

                // save it in root directory
                prop.store(output, null);
                System.out.println("[MusicCore] Configuration file successfully created!");
                System.exit(0);
            }

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getProperties(String name) {

        Properties prop = new Properties();
        try {
            InputStream stream = new FileInputStream("config.properties");
            prop.load(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(name);
    }

    public boolean hasPermission(Member member) {
        String roles[] = getProperties("roles").split(",");
        for (int i = 0; i < member.getRoles().size(); i++) {
            for (int e = 0; e < roles.length; e++) {
                if (member.getRoles().get(i).getName().equals(roles[e])) {
                    return true;
                }
            }
        }
        return false;
    }
}