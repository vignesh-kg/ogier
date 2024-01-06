package org.ogier.msbuilder.util.implementation;

import org.ogier.msbuilder.constants.OgierConstants;
import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.ogier.msbuilder.util.interfaces.IPomAdder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

import static java.lang.System.exit;

@Component
public class DirectoryUtil implements IDirectoryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryUtil.class);

    @Autowired
    private OgierConfiguration ogierConfiguration;

    @Autowired
    private IPomAdder pomAdder;

    @Override
    public void createModules(Map<String, String> argMap) {
        LOGGER.info(String.join(",", ogierConfiguration.getModules()));
        List<String> modulesToCreate = getModulesToCreate(argMap);
        String directoryPath = getDirectoryPath(argMap);
        String msName = argMap.get(OgierConstants.KEY_MSNAME);
        String groupId = argMap.get(OgierConstants.KEY_GROUPID);
        createRootDirectoryAndAddReactorPOM(directoryPath + msName, msName, modulesToCreate, groupId);

        modulesToCreate.forEach(moduleToCreate -> {
            String folderName = msName + "-" + moduleToCreate;
            String rootDirectoryPath = directoryPath + msName + "/" + folderName;
            createDirectory(rootDirectoryPath);
            createSubFolders(groupId, moduleToCreate, rootDirectoryPath);
            pomAdder.addPom(rootDirectoryPath, msName, moduleToCreate, groupId);
        });
        LOGGER.info("Modules created successfully in {}", directoryPath);
    }

    private void createRootDirectoryAndAddReactorPOM(String directoryPath, String msName, List<String> modules, String groupId) {
        createDirectory(directoryPath);
        pomAdder.createReactorPOM(directoryPath,msName,modules,groupId);
    }

    private void createSubFolders(String groupId, String module, String rootDirectoryPath)
    {
        LOGGER.info("{}",ogierConfiguration.getExcludesubmodules());
        if(!ogierConfiguration.getExcludesubmodules().contains(module))
        {
            String copyGroupId = groupId.replace(".", "/");
            if(!"test".equalsIgnoreCase(module))
            {
                String resourcesFolderPath = rootDirectoryPath + "/" + OgierConstants.SRC_MAIN_RESOURCES;
                createDirectory(resourcesFolderPath);
                String subFolderPath = rootDirectoryPath + "/" + OgierConstants.SRC_MAIN_JAVA + "/" + copyGroupId + "/" + module;
                createDirectory(subFolderPath);
            }
            else
            {
                String resourcesFolderPath = rootDirectoryPath + "/" + OgierConstants.SRC_TEST_RESOURCES;
                createDirectory(resourcesFolderPath);
                String subFolderPath = rootDirectoryPath + "/" + OgierConstants.SRC_TEST_JAVA + "/" + copyGroupId + "/" + module;
                createDirectory(subFolderPath);
            }
        }
    }

    @Override
    public void createDirectory(String directoryPath) {
        File newDirectory = new File(directoryPath);
        if (!newDirectory.isDirectory()) {
            try {
                newDirectory.mkdirs();
                LOGGER.info("{} created successfully", directoryPath);
            } catch (Exception e) {
                LOGGER.error("Error creating Directory: {}, exception {}", directoryPath, e.toString());
                exit(1);
            }
        } else {
            LOGGER.info("Directory {} already exist!!", directoryPath);
        }
    }

    private String getDirectoryPath(Map<String, String> argMap) {
        String directoryPath = argMap.get(OgierConstants.KEY_DIRECTORYPATH);

        if (!StringUtils.hasText(directoryPath)) {

        } else {

        }

        return directoryPath;
    }

    /**
     * To create Final List to Modules to be generated
     * by adding additional modules to defaultModules and remove
     * exclude Modules from the list.
     *
     * @param argMap
     * @return
     */
    private List<String> getModulesToCreate(Map<String, String> argMap) {
        List<String> defaultModules = ogierConfiguration.getModules();
        List<String> excludedModules = new ArrayList<>();
        List<String> additionalModules = new ArrayList<>();

        String modulesToExclude = argMap.get(OgierConstants.KEY_EXCLUDEMODULES);
        if (StringUtils.hasText(modulesToExclude)) {
            for (String moduleToExclude : modulesToExclude.split(",")) {
                String module = moduleToExclude.trim();
                if (!ogierConfiguration.getMandatorymodules().contains(module)) {
                    excludedModules.add(module);
                } else {
                    LOGGER.info("{} is a mandatory Module. Cannot exclude", module);
                }
            }
            LOGGER.info("Excluded Modules: {}", excludedModules);
        }

        String additionalModulesToCreate = argMap.get(OgierConstants.KEY_ADDITIONALMODULES);
        if (StringUtils.hasText(additionalModulesToCreate)) {
            for (String additionalModuleToCreate : additionalModulesToCreate.split(",")) {
                additionalModules.add(additionalModuleToCreate.trim());
            }
            LOGGER.info("Additional Modules to be created: {}", additionalModules);
        }

        Set<String> finalListOfModulesToBeCreated = new HashSet<>();
        finalListOfModulesToBeCreated.addAll(defaultModules);
        finalListOfModulesToBeCreated.addAll(additionalModules);
        excludedModules.forEach(finalListOfModulesToBeCreated::remove);

        return new ArrayList<>(finalListOfModulesToBeCreated);
    }
}