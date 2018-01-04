package com.my.hello;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class HelloAgent {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName helloName = new ObjectName("jmxBean:name=hello");
        server.registerMBean(new Hello(), helloName);

        ObjectName adapterName = new ObjectName("HelloAgent:name=htmladapter,port=8082");

        HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
        server.registerMBean(htmlAdaptorServer, adapterName);
        htmlAdaptorServer.start();

        //Thread.sleep(60 * 60 * 1000);
    }
}
