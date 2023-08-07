package ru.team38.userservice.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ru.team38.common.aspects", "ru.team38.common.repository", "ru.team38.common.services"})
public class ImportCommonConfig {
}
