# CH09. Spring MVC 동작방식

---

## 1. 스프링 MVC 핵심 구성 요소

### DispatcherServlet

모든 연결을 담당한다. 클라이언트의 요청을 전달받아 요청에 맞는 컨트롤러가 리턴한 결과값을 View에 전달하여 알맞은 응답을 생성한다.

### HandlerMapping

클라이언트의 요청 URL을 어떤 컨트롤러가 처리할지 결정한다.

### Controller

클라이언트의 요청을 처리한 뒤 결과를 `DispatcherServlet`에 리턴한다.

### ModelAndView

컨트롤러가 처리한 결과 정보 및 뷰 선택에 필요한 정보를 담는다.

### ViewResolver

컨트롤러의 처리 결과를 생성할 뷰를 결정한다.

### View

컨트롤러의 처리 결과 화면을 생성한다. JSP 또는 템플릿 파일 등을 뷰로 사용한다.

## 2. HandlerMapping 종류

- `BeanNameUrlHandlerMapping`: 빈 이름을 URL로 사용하는 매핑 전략
- `ControllerClassNameHandlerMapping`: URL과 일치하는 클래스 이름을 갖는 빈을 컨트롤러로 사용
- `SimpleUrlHandlerMapping`: URL 패턴에 매핑된 컨트롤러를 사용
- `DefaultAnnotationHandlerMapping`: 어노테이션으로 URL과 컨트롤러를 매핑

현재 Spring MVC에서는 `@Controller`, `@GetMapping`, `@RequestMapping` 기반 매핑을 주로 사용한다.

## 3. 동작 과정

1. 웹 브라우저로부터 요청이 들어온다.
2. `DispatcherServlet`은 요청을 처리할 컨트롤러 객체를 검색한다.
3. 이때 직접 검색하지 않고 `HandlerMapping` 빈 객체에게 컨트롤러 검색을 요청한다.
4. `HandlerMapping`은 요청 경로를 이용해 이를 처리할 컨트롤러 빈 객체를 `DispatcherServlet`에 전달한다.
5. `DispatcherServlet`은 `HandlerAdapter` 빈에게 요청 처리를 위임한다.
6. `HandlerAdapter`는 컨트롤러의 알맞은 메서드를 호출해서 요청을 처리한다.
7. 컨트롤러의 처리 결과를 `HandlerAdapter`에 리턴한다.
8. `HandlerAdapter`는 처리 결과를 `ModelAndView` 객체로 변환해서 `DispatcherServlet`에 리턴한다.
9. `DispatcherServlet`은 `ViewResolver`를 사용해서 결과를 보여줄 뷰를 찾는다.
10. `ViewResolver`는 뷰 이름에 해당하는 `View` 객체를 찾거나 생성해서 리턴한다.
11. `View`가 최종 응답 화면을 생성한다.

## 4. Controller와 Handler

- `Controller`: 클라이언트의 요청을 실제로 처리한다.
- `DispatcherServlet`: 요청을 전달받는 창구 역할을 한다.
- `Handler`: 웹 요청을 실제로 처리하는 객체를 의미한다.
- `HandlerMapping`: 특정 요청 경로를 처리해주는 핸들러를 찾아주는 객체이다.
- `HandlerAdapter`: 핸들러의 실행 결과를 `ModelAndView`로 변환해주는 객체이다.

`@Controller` 적용 객체나 `Controller` 인터페이스를 구현한 객체는 모두 핸들러가 될 수 있다.

## 5. DispatcherServlet과 스프링 컨테이너

`HandlerMapping`, `HandlerAdapter`, `Controller`, `ViewResolver` 등의 빈은 `DispatcherServlet`이 생성한 스프링 컨테이너에서 구한다.

따라서 설정 파일에 이들 빈에 대한 정보가 포함되어 있어야 한다.

## 6. @Controller를 위한 HandlerMapping과 HandlerAdapter

`@Controller` 객체는 `DispatcherServlet` 입장에서 보면 한 종류의 핸들러 객체이다.

`@EnableWebMvc`가 추가해주는 클래스 중에는 `@Controller` 타입의 핸들러 객체를 처리하기 위한 아래 두 클래스가 포함된다.

- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping`
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter`

## 7. WebMvcConfigurer 인터페이스

`@EnableWebMvc` 어노테이션을 사용하면 `WebMvcConfigurer` 타입인 빈 객체의 메서드를 호출하여 MVC 설정을 추가한다.

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

## 8. JSP를 위한 ViewResolver

`configureViewResolvers()`의 `viewResolverRegistry`를 이용하면 내부적으로 다음과 같은 ViewResolver가 등록된다.

```java
@Bean
public ViewResolver viewResolver() {
    InternalResourceViewResolver vr = new InternalResourceViewResolver();
    vr.setPrefix("/WEB-INF/views/");
    vr.setSuffix(".jsp");
    return vr;
}
```

컨트롤러가 리턴한 문자열 `hello`는 JSP 경로로 변환된다.

```text
hello -> /WEB-INF/views/hello.jsp
```

## 9. Model 데이터 전달

```java
@GetMapping("/hello")
public String hello(Model model, @RequestParam(value = "name", required = false) String name) {
    model.addAttribute("greeting", "안녕하세요, " + name);
    return "hello";
}
```

`greeting` 키를 갖는 값이 View에 전달된다.

```html
인사말 : ${greeting}
```

## 10. 디폴트 핸들러와 HandlerMapping 우선순위

매핑 경로가 `/`인 경우 `.jsp`로 끝나는 요청을 제외한 모든 요청을 `DispatcherServlet`이 처리한다.

`@EnableWebMvc`가 등록하는 `HandlerMapping`은 `@Controller` 어노테이션을 적용한 빈 객체가 처리할 수 있는 요청 경로만 대응할 수 있다.

`/index.html` 같은 요청을 처리할 컨트롤러 객체를 찾지 못하면 404 응답이 발생한다.

이런 경로는 `WebMvcConfigurer`의 `configureDefaultServletHandling()` 메서드를 사용해 처리할 수 있다.

```java
@Override
public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
}
```

`DefaultServletHandlerConfigurer#enable()`은 아래 두 객체를 추가한다.

- `DefaultServletHttpRequestHandler`: 클라이언트의 모든 요청을 WAS가 제공하는 디폴트 서블릿에 전달한다.
- `SimpleUrlHandlerMapping`: 모든 경로 `/**`를 `DefaultServletHttpRequestHandler`로 처리하도록 설정한다.

## 11. 우선순위

1. `RequestMappingHandlerMapping`을 사용해 요청을 처리할 핸들러를 검색한다.
2. 존재하면 해당 컨트롤러를 이용해서 요청을 처리한다.
3. 존재하지 않으면 `SimpleUrlHandlerMapping`을 사용해 핸들러를 검색한다.
4. `DefaultServletHttpRequestHandler`가 디폴트 서블릿에 처리를 위임한다.
