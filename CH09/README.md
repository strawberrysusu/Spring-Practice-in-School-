# CH09. Spring MVC 동작방식

이 챕터는 CH08에서 만든 Spring MVC 예제를 기준으로 동작 흐름을 이해하는 내용이다.

## 핵심 구성 요소

- `DispatcherServlet`: 모든 웹 요청을 받는 앞단 서블릿
- `HandlerMapping`: 요청 URL을 처리할 핸들러를 찾음
- `Controller`: 실제 요청 처리
- `HandlerAdapter`: 핸들러를 실행하고 결과를 `ModelAndView` 형태로 맞춤
- `ModelAndView`: 모델 데이터와 뷰 이름을 담음
- `ViewResolver`: 뷰 이름을 실제 JSP 경로로 변환
- `View`: 최종 응답 화면 생성

## CH08 예제 기준 동작 과정

1. 브라우저에서 `/hello?name=홍길동` 요청
2. `DispatcherServlet`이 요청을 받음
3. `HandlerMapping`이 `/hello`를 처리할 컨트롤러를 검색
4. `HelloController`의 `hello()` 메서드를 찾음
5. `HandlerAdapter`가 컨트롤러 메서드를 실행
6. 컨트롤러가 `Model`에 `greeting` 값을 담고 `"hello"`를 반환
7. `ViewResolver`가 `"hello"`를 `/WEB-INF/views/hello.jsp`로 변환
8. JSP가 `${greeting}` 값을 출력

## Controller와 Handler

- `Controller`: 클라이언트 요청을 실제로 처리하는 객체
- `Handler`: 웹 요청을 처리하는 실제 객체를 넓게 부르는 말
- `@Controller`가 붙은 객체도 핸들러가 될 수 있다.

## @EnableWebMvc가 등록하는 주요 객체

`@EnableWebMvc`는 `@Controller` 기반 요청 처리를 위해 필요한 MVC 기본 빈들을 등록한다.

- `RequestMappingHandlerMapping`
- `RequestMappingHandlerAdapter`

## WebMvcConfigurer

```java
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

## JSP ViewResolver

```java
viewResolverRegistry.jsp("/WEB-INF/views/", ".jsp");
```

위 설정 때문에 컨트롤러가 `"hello"`를 반환하면 실제 JSP 경로는 아래처럼 결정된다.

```text
/WEB-INF/views/hello.jsp
```

## 디폴트 핸들러

`configureDefaultServletHandling()`에서 `configurer.enable()`을 호출하면 Spring MVC가 처리하지 못한 정적 자원 요청을 WAS의 기본 서블릿에 위임할 수 있다.

우선순위는 대략 아래 순서로 이해하면 된다.

1. `RequestMappingHandlerMapping`으로 `@Controller` 요청을 먼저 찾는다.
2. 없으면 `SimpleUrlHandlerMapping`을 통해 기본 서블릿 처리 여부를 확인한다.
3. 기본 서블릿이 정적 자원 요청을 처리한다.

## 관련 코드

CH09 내용은 CH08 예제 코드의 동작 방식을 설명하는 챕터이므로 실제 실행 코드는 `../CH08`에 있다.

- `../CH08/src/main/java/config/MvcConfig.java`
- `../CH08/src/main/java/chapter09/HelloController.java`
- `../CH08/src/main/webapp/WEB-INF/web.xml`
- `../CH08/src/main/webapp/WEB-INF/views/hello.jsp`
