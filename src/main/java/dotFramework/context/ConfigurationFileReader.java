package dotFramework.context;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationFileReader {

    static Properties getConfigProperties() {
        Properties prop = null;

        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try {
            prop = new Properties();
            prop.load(new FileInputStream(rootPath + "/config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
}
