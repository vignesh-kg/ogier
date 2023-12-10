package org.ogier.msbuilder.main;

import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OgierBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OgierBuilder.class);

    @Value("${org.ogier.modules}")
    private String listOfModules;

    @Autowired
    private IDirectoryUtil directoryUtil;

    public void build(String[] args)
    {
        LOGGER.info(listOfModules);
        directoryUtil.createDirectory();
    }
}