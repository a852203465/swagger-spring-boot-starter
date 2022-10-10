package cn.darkjrong.swagger.webmvc;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * springfox请求处理程序
 *  处理swaggerUI不匹配接口
 * @author Rong.Jia
 * @date 2022/10/09
 */
public class SpringfoxRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Override
    public void afterPropertiesSet() {
        config.setTrailingSlashMatch(this.useTrailingSlashMatch());
        config.setContentNegotiationManager(this.getContentNegotiationManager());
        config.setPathMatcher(null);

        super.afterPropertiesSet();
    }

}
