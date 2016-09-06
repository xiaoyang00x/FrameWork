package org.LogFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogFactory {
    private static Logger log = Logger.getLogger(LogFactory.class);

    static {
        PropertyConfigurator
                .configure(Thread.currentThread().getContextClassLoader().getResource("log4j.propertie").getPath());
    }

    public static Logger getLogger(Class<?> clazz) {
        final Logger logger = Logger.getLogger(clazz);
        return logger;
    }
}
