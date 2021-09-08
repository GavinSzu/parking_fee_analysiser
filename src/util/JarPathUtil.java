package util;

import java.io.File;
import java.net.URL;

public class JarPathUtil {

    public static String ROOT_PATH = null;

    public static String getJieShunFolderPath() {
        return getRootPath() + "\\jieshun";
    }

    public static String getYinLianFolderPath() {
        return getRootPath() + "\\yinlian";
    }

    public static String getRootPath() {
        if (null == ROOT_PATH) {
            ROOT_PATH = getPath();
        }
        return ROOT_PATH;
    }

    /**
     * 获取项目加载类的根路径
     * @return
     */
    private static String getPath() {
        String path = "";
        try {
            //jar中没有目录的概念
            URL location = JarPathUtil.class.getProtectionDomain().getCodeSource().getLocation();//获得当前的URL
            File file = new File(location.getPath());//构建指向当前URL的文件描述符
            if (file.isDirectory()) {//如果是目录,指向的是包所在路径，而不是文件所在路径
                path = file.getAbsolutePath();//直接返回绝对路径
            } else {//如果是文件,这个文件指定的是jar所在的路径(注意如果是作为依赖包，这个路径是jvm启动加载的jar文件名)
                path = file.getParent();//返回jar所在的父路径
            }
            System.out.println("path：" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}
