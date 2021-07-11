package cn.darkjrong.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * swagger资源配置
 *
 * @author Rong.Jia
 */
@Configuration
@ConditionalOnClass({WebFluxConfigurer.class})
public class SwaggerWebFluxConfiguration implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {


    }

}
