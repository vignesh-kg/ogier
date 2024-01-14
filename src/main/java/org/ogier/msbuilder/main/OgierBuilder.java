package org.ogier.msbuilder.main;

import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OgierBuilder {

    @Autowired
    private IDirectoryUtil directoryUtil;

    public void build(String[] args) throws Exception {
        directoryUtil.createModules(createArgsMap(args));
    }

    private Map<String, String> createArgsMap(String[] args) throws Exception {
        Map<String, String> argsMap = new HashMap<>();

        for(String arg : args)
        {
            String[] argSplited = arg.split("=");
            argsMap.put(argSplited[0].toLowerCase(),argSplited[1]);
        }
        validateArgs(argsMap.keySet());
        return argsMap;
    }

    private void validateArgs(Set<String> args) throws Exception {
        List<String> mandatoryArgs = new ArrayList<>(Arrays.asList("msName", "directoryPath", "groupId"));

        for(String mandatoryArg : mandatoryArgs)
        {
            if(!args.contains(mandatoryArg))
            {
                throw new Exception("Missing Mandatory argument " + mandatoryArg);
            }
        }
    }
}