# CH08. Spring-MVC 시작하기

## 실행 흐름

1. Tomcat9에 `CH08` WAR 프로젝트를 등록한다.
2. 브라우저에서 `/hello?name=홍길동`으로 요청한다.
3. `DispatcherServlet`이 요청을 받는다.
4. `HelloController`의 `hello()` 메서드가 실행된다.
5. `Model`에 `greeting` 값을 담는다.
6. `hello` 뷰 이름을 반환한다.
7. `/WEB-INF/views/hello.jsp`가 화면을 만든다.

## 주요 파일

- `pom.xml`: WAR 패키징, Servlet/JSP/JSTL, Spring Web MVC 의존성
- `src/main/java/config/MvcConfig.java`: Spring MVC 활성화, JSP ViewResolver 설정
- `src/main/java/config/ControllerConfig.java`: Controller 빈 등록
- `src/main/java/chapter09/HelloController.java`: `/hello` 요청 처리
- `src/main/webapp/WEB-INF/web.xml`: DispatcherServlet 등록
- `src/main/webapp/WEB-INF/views/hello.jsp`: 화면 출력
