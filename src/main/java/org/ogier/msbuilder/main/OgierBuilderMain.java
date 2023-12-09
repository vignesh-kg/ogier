package org.ogier.msbuilder.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OgierBuilderMain
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OgierBuilderMain.class);

    public static void main(String args[])
    {
        LOGGER.info("Ogier is starting to build your Microservice!!!");
        for(String arg : args)
        {
            LOGGER.info(arg);
        }
        try
        {

        }
        catch (Exception ex)
        {
            LOGGER.error("Ogier is in trouble : {}",ex);
            System.exit(1);
        }
    }
}