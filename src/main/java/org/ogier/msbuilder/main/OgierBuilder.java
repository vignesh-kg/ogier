package org.ogier.msbuilder.main;

import org.ogier.msbuilder.util.implementation.OgierConfiguration;
import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OgierBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OgierBuilder.class);

    @Autowired
    private OgierConfiguration ogierConfiguration;

    @Autowired
    private IDirectoryUtil directoryUtil;

    public void build(String[] args)
    {
        LOGGER.info(String.join(",",ogierConfiguration.getModules()));
        directoryUtil.createDirectory();
    }
}