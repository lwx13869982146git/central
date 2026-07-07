package com.central.workflow.config;

import org.springframework.context.annotation.Configuration;

/**
 * Flowable配置类
 *
 * Flowable 6.x 的 spring-boot-starter-process 已经自动配置了
 * ProcessEngine, RuntimeService, TaskService 等核心组件。
 * 此处可添加自定义配置。
 *
 * @author author
 * @date 2026/07/06
 */
@Configuration
public class FlowableConfig {
    // Flowable auto-configuration handles most settings via spring.flowable.* properties
}
