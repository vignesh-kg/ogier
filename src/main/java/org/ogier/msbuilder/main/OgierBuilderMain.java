package org.ogier.msbuilder.main;

import org.ogier.msbuilder.config.YamlPropertyLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {"classpath:main-config.yaml"}, factory = YamlPropertyLoaderFactory.class)
@SpringBootApplication
public class OgierBuilderMain
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OgierBuilderMain.class);

    public static void main(String args[])
    {
        LOGGER.info("Ogier is starting to build your Microservice!!!");
        ApplicationContext context = SpringApplication.run(OgierBuilderMain.class, args);

        for(String arg : args)
        {
            LOGGER.info(arg);
        }
        try
        {
            context.getBean(OgierBuilder.class).build(args);
        }
        catch (Exception ex)
        {
            LOGGER.error("Ogier is in trouble : {}",ex);
            System.exit(1);
        }
    }
}