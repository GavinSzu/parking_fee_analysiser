package util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    public static final String JIESHUN_FLODER = "jieshun.folder";
    public static final String YINLIAN_FLODER = "yinlian.folder";
    public static final String ANALYSE_RS_FLODER = "analyse.rs";

    public static Map<String, String> propMap = new HashMap<>();

    public static void initProperties() throws Exception {
        if (!propMap.isEmpty()) {
            return;
        }
        // 用classLoader加载配置文件，默认src目录为classPath目录，注意src下的文件夹前不能加“/”，否则会认为是绝对路径
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("resources/application.properties");

        try {
            Properties pro = new Properties();
            pro.load(in);
            Enumeration en = pro.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String property = pro.getProperty(key);
                propMap.put(key, property);
            }
        } finally {
            in.close();
        }
    }

    public static String getPropByKey(String key) {
        return propMap.get(key);
    }

}
