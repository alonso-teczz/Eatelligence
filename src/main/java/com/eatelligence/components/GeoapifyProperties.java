package com.eatelligence.components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "geoapify.api")
@Getter @Setter
public class GeoapifyProperties {
    private String key;
}

