package properties;

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
}