package com.my.springmvc.servlet;

import com.my.springmvc.annotation.Controller;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neil on 2018/1/21.
 */
@WebServlet(name = "dispatcherServlet", urlPatterns = "/*", loadOnStartup = 1,
    initParams = {@WebInitParam(name = "base-package", value = "com.my.springmvc")})
public class DispatcherServlet extends HttpServlet {

    //扫描的基包
    private String basePackage = "";
    //基包下所有的带包路径全限定类名
    private List<String> packageNames = new ArrayList<String>();
    //注解实例化，注解上的名称：实例化对象
    private Map<String, Object> instanceMap = new HashMap<String, Object>();
    //带包路径全限定类名： 注解实例化
    private Map<String, Object> nameMap = new HashMap<String, Object>();
    //URL地址和方法的映射关系， springMVC就是方法调用链
    private Map<String, Method> urlMethodMap = new HashMap<String, Method>();
    //Method和全限定类名映射的关系， 主要是为了通过Method找到该方法的对象利用反射执行
    private Map<Method, String> methodPackageMap = new HashMap<Method, String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage = config.getInitParameter("base-package");

        //1.扫描基包得到全部的带包路径全限定名
        scanBasePackage(basePackage);
        //2.把带@Controller,@Service,@Repository的类实例化放入map中，key为注解上的名称
        try {
            instance(packageNames);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void instance(List<String> packageNames) throws ClassNotFoundException {
        if (packageNames.size() < 1) {
            return;
        }
        for (String string : packageNames) {
            Class clzss = Class.forName(string);
            if (clzss.isAnnotationPresent(Controller.class)) {

            }
        }
    }

    private void scanBasePackage(String basePackage) {
        //注意为了得到基包下面的URL路径，需要对base-package做转换，将"."替换成"/"
        //基包是X.Y.Z的形式，而URL是X/Y/Z的形式，需要转换。
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File basePackageFile = new File(url.getPath());
        System.out.println("scan:" + basePackageFile);

        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles) {
            if (file.isDirectory()) { //目录继续递归扫描
                scanBasePackage(basePackage + "." + file.getName());
            } else {
                //类似com.my.springmvc.service.impl.UserServiceImpl.class去掉class
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }
}
