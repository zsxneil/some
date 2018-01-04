package com.my.hello;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class HelloJackAgent {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName helloName = new ObjectName("neil:name=Hello");
        Hello hello = new Hello();
        server.registerMBean(hello, helloName);

        Jack jack = new Jack();
        server.registerMBean(jack, new ObjectName("jack:name=Jack"));
        jack.addNotificationListener(new HelloListener(), null, hello);
        Thread.sleep(5000000);
    }
}
