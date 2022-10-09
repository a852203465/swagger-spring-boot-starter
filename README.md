# Swagger Spring Starter

## 版本基础
    - SpringFox Swagger：3.0.0
    - Spring Boot 2.7.4

## 使用说明
- 在`pom.xml`中引入依赖：
```xml
<dependency>
	<groupId>cn.darkjrong</groupId>
	<artifactId>swagger-spring-boot-starter</artifactId>
	<version>1.1</version>
</dependency>

<!-- webmvc -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- webflux -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## 注意
 1. webflux, webmvc 环境可自由切换


## 参数配置

### 默认配置
```yaml
swagger:
  production: false  # 是否是生产环境
  title: swagger测试
  description: swagger测试接口文档
  base-packages: com.example.swaggerdemo.controller1
  path-mapping: /interaction
  version: 1.0
  host: localhost:8080
  license: Apache License, Version 2.0
  license-url: https://www.apache.org/licenses/LICENSE-2.0.html
  terms-of-service-url: https://www.apache.org/licenses/LICENSE-2.0.html
  base-path: /**
  contact:
    name: aa
    email: A@xdcplus.com
    url: https://www.a.com
```

### 分组配置
```yaml
swagger:
  production: false
  docket:
    test: # 分组名
      groupName: 测试  # 分组名
      base-packages: com.example.swaggerdemo.controller
      path-mapping: /test
      title: swagger测试
      description: swagger测试接口文档
      version: 1.0
      host: localhost:8080
      license: Apache License, Version 2.0
      license-url: https://www.apache.org/licenses/LICENSE-2.0.html
      terms-of-service-url: https://www.apache.org/licenses/LICENSE-2.0.html
      base-path: /**
      contact:
        name: 贾荣
        email: Rong.Jia@xdcplus.com
        url: https://www.xdcplus.com
    test2:
      groupName: 测试2  # 分组名
      base-packages: com.example.swaggerdemo.controller2
      path-mapping: /test
      title: swagger测试
      description: swagger测试接口文档
      version: 1.0
      host: localhost:8080
      license: Apache License, Version 2.0
      license-url: https://www.apache.org/licenses/LICENSE-2.0.html
      terms-of-service-url: https://www.apache.org/licenses/LICENSE-2.0.html
      base-path: /**
      contact:
        name: 贾荣
        email: Rong.Jia@xdcplus.com
        url: https://www.xdcplus.com


```

































