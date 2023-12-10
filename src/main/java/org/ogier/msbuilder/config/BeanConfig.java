package org.ogier.msbuilder.config;

import org.ogier.msbuilder.util.implementation.DirectoryUtil;
import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public IDirectoryUtil directoryUtil() {
        return new DirectoryUtil();
    }
}