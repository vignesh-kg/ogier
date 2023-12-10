package org.ogier.msbuilder.config;

import org.ogier.msbuilder.util.implementation.DirectoryUtil;
import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:main-config.yaml"}, factory = YamlPropertyLoaderFactory.class)
public class BeanConfig {

    @Bean
    public IDirectoryUtil directoryUtil() {
        return new DirectoryUtil();
    }
}