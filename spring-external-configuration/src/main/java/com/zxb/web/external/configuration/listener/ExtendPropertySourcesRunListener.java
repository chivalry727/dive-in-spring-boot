package com.zxb.web.external.configuration.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展 {@link PropertySources}
 * 基于SpringApplicationRunListener.environmentPrepared 扩展外部化配置属性源
 *
 * @author Mr.zxb
 * @date 2019-09-10 14:55
 */
public class ExtendPropertySourcesRunListener implements SpringApplicationRunListener, Ordered {

    private final SpringApplication application;

    private final String[] args;

    public ExtendPropertySourcesRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        final MutablePropertySources propertySources = environment.getPropertySources();

        Map<String, Object> source = new HashMap<>(16);
        // 1.from-environmentPrepared 0
        // application.properties   1
        // META-INF/default.properties  7
        source.put("user.id", 1);
        MapPropertySource mapPropertySource = new MapPropertySource("from-environmentPrepared", source);

        propertySources.addFirst(mapPropertySource);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
        Map<String, Object> source = new HashMap<>(16);
        // 1.from-contextPrepared : -1
        // 2.from-ApplicationContextInitializer : 0
        // 3.from-ApplicationEnvironmentPreparedEvent 1
        // 4.from-environmentPrepared 2
        // 5.from-EnvironmentPostProcessor 3
        // application.properties   1
        // META-INF/default.properties  7
        source.put("user.id", -1);
        // 排在最后的覆盖前面的
        propertySources.addFirst(new MapPropertySource("from-contextPrepared", source));
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
        Map<String, Object> source = new HashMap<>(16);
        // 1.from-contextLoaded : -2
        // 1.from-contextPrepared : -1
        // 2.from-ApplicationContextInitializer : 0
        // 3.from-ApplicationEnvironmentPreparedEvent 1
        // 4.from-environmentPrepared 2
        // 5.from-EnvironmentPostProcessor 3
        // application.properties   1
        // META-INF/default.properties  7
        source.put("user.id", -2);
        // 排在最后的覆盖前面的
        propertySources.addFirst(new MapPropertySource("from-contextLoaded", source));
    }

    @Override
    public void started(ConfigurableApplicationContext context) {

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

    @Override
    public int getOrder() {
        // 确保在 EventPublishingRunListener 之后执行
        return new EventPublishingRunListener(application, args).getOrder() + 1;

    }
}
