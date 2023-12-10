package org.ogier.msbuilder.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:main-config.yaml"}, factory = YamlPropertyLoaderFactory.class)
@ComponentScan(basePackages = {"org.ogier.msbuilder.util"})
public class BeanConfig {
}