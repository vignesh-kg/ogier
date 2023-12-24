package org.ogier.msbuilder.main;

import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OgierBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(OgierBuilder.class);

    @Autowired
    private IDirectoryUtil directoryUtil;

    public void build(String[] args) {
        directoryUtil.createModules(createArgsMap(args));
    }

    private Map<String, String> createArgsMap(String[] args)
    {
        Map<String, String> argsMap = new HashMap<>();

        for(String arg : args)
        {
            String[] argSplited = arg.split("=");
            argsMap.put(argSplited[0].toLowerCase(),argSplited[1]);
        }

        return argsMap;
    }
}