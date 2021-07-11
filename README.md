# swagger spring boot starter

## 1. 版本说明
 - knife4j: 3.0.3
 - swagger: 3.0.0

## 2. 使用方式
1. 下载源码 
 - 下载源码 并install，或者推送到私库引入使用
 
2. 引入依赖
```xml
        
        <!-- swagger -->
        <dependency>
            <groupId>cn.darkjrong</groupId>
            <artifactId>swagger-spring-boot-starter</artifactId>
            <version>1.0</version>
        </dependency>
    
        <!-- WebMvc环境引入 -->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-spring-boot-starter</artifactId>
            <version>3.0.3</version>
        </dependency>
        
        <!-- webflux环境引入 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
            <version>3.0.0</version>
        </dependency>

```

3. 配置参数(application.properties) yml配置
```yaml
# swagger
swagger:
  title: xxxxx
  description: xxxx接口文档系统
  base-packages: cn.darkjrong.demo.controller
  version: 1.0.0
  license: Apache License, Version 2.0
  license-url: https://www.apache.org/licenses/LICENSE-2.0.html
  terms-of-service-url: https://www.apache.org/licenses/LICENSE-2.0.html
  base-path: /**
  contact:
    name: 贾荣
    email: 852203465@qq.com
    url: https://github.com/a852203465
```

3. 访问swagger
 - http://ip:port/doc.html

 


