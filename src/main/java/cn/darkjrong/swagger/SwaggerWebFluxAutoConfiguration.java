package cn.darkjrong.swagger;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * swagger配置
 *
 * @author Rong.Jia
 */
@EnableOpenApi
@Configuration
@AllArgsConstructor
@ConditionalOnClass({WebFluxConfigurer.class})
@EnableConfigurationProperties(SwaggerProperties.class)
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerWebFluxAutoConfiguration implements BeanFactoryAware {

    private BeanFactory beanFactory;
    private final SwaggerProperties swaggerProperties;

    private final Environment environment;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public Docket createRestApi() {

        ApiSelectorBuilder apis = new Docket(DocumentationType.OAS_30)
                .host(swaggerProperties.getHost())
                .apiInfo(SwaggerUtils.apiInfo(swaggerProperties))
                .ignoredParameterTypes(SwaggerUtils.getIgnoredParameterTypes(swaggerProperties.getIgnoredParameterTypes())).select()
                .apis(SwaggerUtils.basePackages(swaggerProperties.getBasePackages()));

        SwaggerUtils.getBasePath(swaggerProperties.getBasePath()).forEach(p -> apis.paths(PathSelectors.ant(p)));
        SwaggerUtils.getExcludePath(swaggerProperties.getExcludePath()).forEach(p -> apis.paths(PathSelectors.ant(p).negate()));

        return apis.build()
                .securityContexts(SwaggerUtils.securityContexts(swaggerProperties))
                .securitySchemes(SwaggerUtils.securitySchemas(swaggerProperties));

    }


}
