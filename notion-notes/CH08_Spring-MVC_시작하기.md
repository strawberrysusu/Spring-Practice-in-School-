# CH08. Spring-MVC 시작하기

---

## 1. 프로젝트 구조

- `src/main/java`
- `src/main/webapp`
- `src/main/webapp/WEB-INF`
- `src/main/webapp/WEB-INF/views`

```xml
<packaging>war</packaging>
```

웹 프로젝트이므로 Maven 패키징은 `war`로 설정한다.

## 2. 의존성

```xml
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>javax.servlet-api</artifactId>
  <version>3.1.0</version>
</dependency>
<dependency>
  <groupId>javax.servlet.jsp</groupId>
  <artifactId>javax.servlet.jsp-api</artifactId>
  <version>2.3.1</version>
</dependency>
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>jstl</artifactId>
  <version>1.2</version>
</dependency>
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-webmvc</artifactId>
  <version>5.3.15</version>
</dependency>
```

- `javax.servlet-api`, `javax.servlet.jsp-api`, `jstl`: 웹 프로젝트에 필요한 서블릿/JSP 관련 라이브러리
- `spring-webmvc`: Spring MVC 프레임워크 관련 라이브러리

## 3. Tomcat9

Apache Tomcat 9 다운로드:

https://tomcat.apache.org/download-90.cgi

ZIP 파일 다운로드 후 D 드라이브에 압축을 풀고 폴더명을 `Tomcat9`로 둔다.

## 4. MvcConfig

```java
package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
        viewResolverRegistry.jsp("/WEB-INF/views/", ".jsp");
    }
}
```

- `@EnableWebMvc`: Spring MVC를 활성화한다.
- `WebMvcConfigurer`: Spring MVC 개별 설정을 조정할 때 사용한다.
- `configureDefaultServletHandling()`: 정적 자원을 WAS의 기본 서블릿에 위임한다.
- `configureViewResolvers()`: JSP 뷰 경로와 확장자를 설정한다.

## 5. web.xml

Spring MVC가 웹 요청을 처리하려면 `DispatcherServlet`을 통해 웹 요청을 받아야 한다.

```xml
<servlet>
  <servlet-name>dispatcher</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextClass</param-name>
    <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
  </init-param>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>
      config.MvcConfig
      config.ControllerConfig
    </param-value>
  </init-param>
  <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
  <servlet-name>dispatcher</servlet-name>
  <url-pattern>/</url-pattern>
</servlet-mapping>
```

사용자의 모든 요청을 `DispatcherServlet`에 전달한다.

## 6. Controller 작성

```java
package chapter09;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model, @RequestParam(value = "name", required = false) String name) {
        System.out.println("Hello Controller >>> ");
        model.addAttribute("greeting", "안녕하세요, " + name);
        return "hello";
    }
}
```

```java
package config;

import chapter09.HelloController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    @Bean
    public HelloController helloController() {
        return new HelloController();
    }
}
```

## 7. View 작성

`/WEB-INF/views/hello.jsp`

```html
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Hello</title>
    </head>
    <body>
    인사말 : ${greeting}
    </body>
</html>
```

## 8. 오류 참고

### Already address in use

```powershell
netstat -nao | findstr 8080
```

해당 PID 확인 후 작업 관리자에서 PID 작업을 종료한다.

### 톰캣 로그 한글 깨짐

VM 옵션에 아래 값을 추가한다.

```text
-Dfile.encoding=UTF-8
```
