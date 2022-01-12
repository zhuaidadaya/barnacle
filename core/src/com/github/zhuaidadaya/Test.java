package com.github.zhuaidadaya;

import com.github.zhuaidadaya.MCH.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Test {
    private static Logger logger = LogManager.getLogger("Teat");

    public static void test() {
        logger.info("az");
        logger.info("az");
        logger.info("az");
        logger.info("az");
        logger.info("az");
        logger.info("az");
        logger.error("az");
        logger.info("{}", "az");
        logger.debug("az");

        System.out.println("az???");

        System.out.println(Test.class.getResource("/log4j.xml"));
    }
}
