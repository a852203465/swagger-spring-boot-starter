package cn.darkjrong.swagger;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.core.env.Environment;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Swagger工具类
 *
 * @author 贾荣
 * @date 2021/05/13 17:22
 */
public class SwaggerUtils {

    private static final String DEFAULT_BASE_PATH = "/**";
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    /**
     * 创建Docket
     *
     * @param swaggerProperties        swagger 属性信息
     * @param environment              环境
     * @param openApiExtensionResolver api扩展解析器
     * @param isWebFlux                是否是webflux， true: 是，false: 否
     * @return {@link Map<String, Docket>}  Docket
     */
    public static Map<String, Docket> createDocket(SwaggerProperties swaggerProperties,
                                                   Environment environment,
                                                   OpenApiExtensionResolver openApiExtensionResolver,
                                                   boolean isWebFlux) {

        Map<String, Docket> docketMap = new HashMap<>();

        Map<String, SwaggerProperties.DocketInfo> dockets = swaggerProperties.getDocket();
        if (MapUtil.isEmpty(dockets)){

            ApiSelectorBuilder apis = new Docket(DocumentationType.OAS_30)
                    .host(swaggerProperties.getHost())
                    .apiInfo(SwaggerUtils.apiInfo(swaggerProperties))
                    .ignoredParameterTypes(SwaggerUtils.getIgnoredParameterTypes(swaggerProperties.getIgnoredParameterTypes())).select()
                    .apis(SwaggerUtils.basePackages(swaggerProperties.getBasePackages()));

            setBasePath(swaggerProperties.getBasePath(), apis);
            setExcludePath(swaggerProperties.getExcludePath(), apis);

            Docket docket = apis.build()
                    .securityContexts(SwaggerUtils.securityContexts(swaggerProperties))
                    .securitySchemes(SwaggerUtils.securitySchemas(swaggerProperties));

            if (!isWebFlux) {
                docket.extensions(openApiExtensionResolver
                        .buildExtensions(SwaggerUtils.getGroupName(environment)));
            }

            docketMap.put("defaultDocket", docket);
        }else {

            docketMap.putAll(dockets.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> {

                        SwaggerProperties.DocketInfo docketInfo = e.getValue();

                        String groupName = docketInfo.getGroupName().isEmpty()
                                ? (SwaggerUtils.isGroupNameNull(environment) ? e.getKey() :
                                SwaggerUtils.getGroupName(environment))
                                : docketInfo.getGroupName();

                        ApiSelectorBuilder apis = new Docket(DocumentationType.OAS_30)
                                .host(swaggerProperties.getHost())
                                .groupName(groupName)
                                .apiInfo(SwaggerUtils.apiInfo(swaggerProperties, docketInfo))
                                .ignoredParameterTypes(SwaggerUtils.getIgnoredParameterTypes(docketInfo.getIgnoredParameterTypes())).select()
                                .apis(SwaggerUtils.basePackages(docketInfo.getBasePackages()));

                        setBasePath(docketInfo.getBasePath(), apis);
                        setExcludePath(docketInfo.getExcludePath(), apis);

                        Docket docket = apis.build()
                                .securityContexts(SwaggerUtils.securityContexts(swaggerProperties))
                                .securitySchemes(SwaggerUtils.securitySchemas(swaggerProperties));

                        if (!isWebFlux) {
                            docket.extensions(openApiExtensionResolver
                                    .buildExtensions(SwaggerUtils.getGroupName(environment)));
                        }

                        return docket;
                    })));

        }

        return docketMap;

    }

    public static void setBasePath(List<String> paths, ApiSelectorBuilder apis) {
        SwaggerUtils.getBasePath(paths).forEach(p -> apis.paths(PathSelectors.ant(p)));
    }

    public static void setExcludePath(List<String> paths, ApiSelectorBuilder apis) {
        SwaggerUtils.getExcludePath(paths).forEach(p -> apis.paths(PathSelectors.ant(p).negate()));
    }

    /**
     * 扫描路径
     *
     * @param basePackages 路径集合
     */
    public static Predicate<RequestHandler> basePackages(final List<String> basePackages) {
        return input -> declaringClass(input).transform(handlerPackage(basePackages)).or(true);
    }

    /**
     * 处理程序包
     *
     * @param basePackages 路径
     * @return {@link Function<Class<?>, Boolean>} 处理函数
     */
    private static Function<Class<?>, Boolean> handlerPackage(final List<String> basePackages) {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackages) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

    public static ApiKey apiKey(String name, String keyName, String passAs) {
        return new ApiKey(name, keyName, passAs);
    }

    public static ApiKey apiKey(String name, String keyName, String passAs, List<VendorExtension> vendorExtensions) {
        return new ApiKey(name, keyName, passAs, vendorExtensions);
    }

    public static ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", ApiKeyVehicle.HEADER.getValue());
    }

    /**
     * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
     */
    public static List<SecurityContext> securityContexts(SwaggerProperties swaggerProperties) {
        return Collections.singletonList(SecurityContext.builder()
                .securityReferences(defaultAuth(swaggerProperties))
                .forPaths(PathSelectors.regex(swaggerProperties.getAuthorization().getAuthRegex()))
                .build());
    }


    public static Class<?>[] getIgnoredParameterTypes(List<Class<?>> ignoredParameterTypes) {
        Class<?>[] array = new Class[ignoredParameterTypes.size()];
        return ignoredParameterTypes.toArray(array);
    }

    /**
     * 默认的全局鉴权策略
     */
    public static List<SecurityReference> defaultAuth(SwaggerProperties swaggerProperties) {
        List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        List<SecurityReference> securityReferenceList = new ArrayList<>();
        List<SwaggerProperties.AuthorizationScope> swaggerScopeList = swaggerProperties.getAuthorization().getAuthorizationScopeList();
        swaggerScopeList.forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        if (authorizationScopeList.size() == 0) {
            authorizationScopeList.add(new AuthorizationScope("global", "accessEverywhere"));
        }
        AuthorizationScope[] authorizationScopes = authorizationScopeList.toArray(new AuthorizationScope[0]);
        swaggerScopeList.forEach(authorizationScope -> securityReferenceList.add(new SecurityReference(authorizationScope.getName(), authorizationScopes)));
        if (securityReferenceList.size() == 0) {
            securityReferenceList.add(new SecurityReference(SwaggerUtils.apiKey().getName(), authorizationScopes));
        }
        return securityReferenceList;
    }

    /**
     * 配置安全策略
     */
    public static List<SecurityScheme> securitySchemas(SwaggerProperties swaggerProperties) {
        List<SwaggerProperties.AuthorizationApiKey> swaggerApiKeyList = swaggerProperties.getAuthorization().getAuthorizationApiKeyList();
        if (swaggerApiKeyList.size() == 0) {
            return Collections.singletonList(SwaggerUtils.apiKey());
        } else {
            List<SecurityScheme> securitySchemeList = new ArrayList<>();
            swaggerApiKeyList.forEach(authorizationApiKey
                    -> securitySchemeList.add(SwaggerUtils.apiKey(authorizationApiKey.getName(),
                    authorizationApiKey.getKeyName(), authorizationApiKey.getPassAs())));
            return securitySchemeList;
        }
    }

    /**
     * 配置基本信息
     */
    public static ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }

    /**
     * 配置基本信息
     */
    public static ApiInfo apiInfo(SwaggerProperties swaggerProperties, SwaggerProperties.DocketInfo docketInfo) {
        return new ApiInfoBuilder()
                .title(docketInfo.getTitle().isEmpty() ? swaggerProperties.getTitle() : docketInfo.getTitle())
                .description(docketInfo.getDescription().isEmpty() ? swaggerProperties.getDescription() : docketInfo.getDescription())
                .version(docketInfo.getVersion().isEmpty() ? swaggerProperties.getVersion() : docketInfo.getVersion())
                .license(docketInfo.getLicense().isEmpty() ? swaggerProperties.getLicense() : docketInfo.getLicense())
                .licenseUrl(docketInfo.getLicenseUrl().isEmpty() ? swaggerProperties.getLicenseUrl() : docketInfo.getLicenseUrl())
                .contact(
                        new Contact(
                                docketInfo.getContact().getName().isEmpty() ? swaggerProperties.getContact().getName() : docketInfo.getContact().getName(),
                                docketInfo.getContact().getUrl().isEmpty() ? swaggerProperties.getContact().getUrl() : docketInfo.getContact().getUrl(),
                                docketInfo.getContact().getEmail().isEmpty() ? swaggerProperties.getContact().getEmail() : docketInfo.getContact().getEmail()
                        )
                )
                .termsOfServiceUrl(docketInfo.getTermsOfServiceUrl().isEmpty() ? swaggerProperties.getTermsOfServiceUrl() : docketInfo.getTermsOfServiceUrl())
				.build();
    }

    /**
     * 獲取組名
     *
     * @param environment 环境
     * @return {@link String} 組名
     */
    public static String getGroupName(Environment environment) {
        String name = environment.getProperty("spring.application.name");
        return StrUtil.isBlank(name) ? "default" : name;
    }

    /**
     * 獲取組名
     *
     * @param environment 环境
     * @return {@link Boolean}
     */
    public static Boolean isGroupNameNull(Environment environment) {
        String name = environment.getProperty("spring.application.name");
        return StrUtil.isBlank(name);
    }

    public static List<String> getBasePath(List<String> basePath) {
        if (CollectionUtil.isEmpty(basePath)) {
            basePath.add(DEFAULT_BASE_PATH);
        }
        return basePath;
    }

    public static List<String> getExcludePath(List<String> excludePath) {
        if (CollectionUtil.isEmpty(excludePath)) {
            excludePath.addAll(DEFAULT_EXCLUDE_PATH);
        }
        return excludePath;
    }


}
