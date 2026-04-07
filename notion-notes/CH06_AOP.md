# CH06. AOP (Aspect-Oriented Programming, 관점 지향 프로그래밍)

---

## 이 장의 핵심 한 줄 요약

> **여러 클래스에 공통으로 들어가는 코드(로깅, 시간 측정, 캐싱 등)를 분리해서 한 곳에서 관리하는 기법이 AOP다.**

---

## 하고 싶은 말이 뭔데?

### 문제 상황

실행 시간을 측정하고 싶다고 하자:

```java
// ImpleCalculator
public long factorial(long num) {
    long start = System.nanoTime();          // 시간 측정 코드
    long result = 1;
    for (long i = 1; i <= num; i++) result *= i;
    long end = System.nanoTime();            // 시간 측정 코드
    System.out.println("실행시간 = " + (end - start));
    return result;
}

// RecCalculator
public long factorial(long num) {
    long start = System.nanoTime();          // 똑같은 코드 반복!
    long result = (num == 0) ? 1 : num * factorial(num - 1);
    long end = System.nanoTime();            // 똑같은 코드 반복!
    System.out.println("실행시간 = " + (end - start));
    return result;
}
```

**문제: 시간 측정 코드가 모든 메서드에 복붙됨. 메서드 100개면? 수정하려면?**

### 해결 1: 데코레이터 패턴 (스프링 없이)

```java
public class ExeTimeCalculator implements Calculator {
    private Calculator delegate;    // 실제 계산기를 감쌈

    public ExeTimeCalculator(Calculator delegate) {
        this.delegate = delegate;
    }

    public long factorial(long num) {
        long start = System.nanoTime();
        long result = delegate.factorial(num);   // 원래 메서드 호출
        long end = System.nanoTime();
        System.out.println("실행시간 = " + (end - start));
        return result;
    }
}

// 사용
Calculator cal = new ExeTimeCalculator(new ImpleCalculator());
cal.factorial(20);
```

한계: 감싸는 클래스를 매번 만들어야 하고, 다른 클래스에 적용하려면 또 만들어야 함.

### 해결 2: AOP (스프링)

```java
@Aspect
public class ExeTimeAspect {
    @Around("execution(public * chapter07..*(..))")   // 어떤 메서드에 적용할지
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();               // 원래 메서드 실행
        } finally {
            long end = System.nanoTime();
            System.out.println("실행시간 = " + (end - start));
        }
    }
}
```

**시간 측정 코드를 딱 한 번만 작성! chapter07 패키지의 모든 public 메서드에 자동 적용!**

---

## 반드시 알아야 할 핵심 개념

### 1. AOP 핵심 용어 (시험에 무조건 나옴!)

| 용어 | 영어 | 의미 | 쉽게 말하면 |
|------|------|------|------------|
| **Aspect** | Aspect | 공통 기능을 모아놓은 클래스 | "시간 측정 담당", "캐싱 담당" |
| **Advice** | Advice | 실제로 실행되는 공통 기능 코드 | Aspect 안의 메서드 |
| **Pointcut** | Pointcut | Advice를 적용할 대상을 선정하는 규칙 | "어떤 메서드에 적용할 건지" |
| **JoinPoint** | JoinPoint | Advice가 적용될 수 있는 지점 | Spring AOP에서는 **메서드 실행 지점** |
| **Weaving** | Weaving | Advice를 실제 코드에 적용하는 과정 | 스프링이 프록시로 연결 |

### 2. @Aspect - Aspect 클래스 정의

```java
@Aspect                        // "이 클래스는 공통 기능 모음이야"
public class ExeTimeAspect {
    // Advice 메서드들...
}
```

### 3. @Around - Advice 정의 + Pointcut 지정

```java
@Around("execution(public * chapter07..*(..))")
public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
    // 메서드 실행 전 처리
    long start = System.nanoTime();
    try {
        Object result = joinPoint.proceed();   // ★ 원래 메서드 실행 ★
        return result;
    } finally {
        // 메서드 실행 후 처리
        long elapsed = System.nanoTime() - start;
        System.out.println(elapsed + " ns");
    }
}
```

**ProceedingJoinPoint:**
- `joinPoint.proceed()` : 원래 대상 메서드를 실행
- `joinPoint.getSignature()` : 메서드 정보 (이름, 클래스 등)
- `joinPoint.getArgs()` : 메서드에 전달된 인자들
- `joinPoint.getTarget()` : 대상 객체

### 4. Pointcut 표현식 (execution)

```
execution(수식어  리턴타입  클래스경로.메서드이름(파라미터))
```

| 표현식 | 의미 |
|--------|------|
| `execution(public * chapter07..*(..))` | chapter07 패키지(하위 포함)의 모든 public 메서드 |
| `execution(public * chapter07..*(long))` | chapter07 패키지의 long 파라미터 1개 받는 public 메서드 |
| `execution(* set*(..))` | 이름이 set으로 시작하는 모든 메서드 |
| `execution(* com.example.Service.*(..))` | Service 클래스의 모든 메서드 |

**표현식 해부:**
```
execution(public * chapter07..*(..))
           |     |    |      | |  |
           |     |    |      | |  +-- (..) = 파라미터 0개 이상 아무거나
           |     |    |      | +-- * = 메서드명 아무거나
           |     |    |      +-- .. = 하위 패키지 포함
           |     |    +-- chapter07 패키지
           |     +-- * = 리턴타입 아무거나
           +-- public = 접근제어자
```

### 5. @EnableAspectJAutoProxy - AOP 활성화

설정 클래스에 반드시 붙여야 AOP가 동작한다!

```java
@Configuration
@EnableAspectJAutoProxy          // ★ 이거 없으면 AOP 안 됨! ★
public class AppContext {
    @Bean
    public ExeTimeAspect exeTimeAspect() {
        return new ExeTimeAspect();       // Aspect도 Bean으로 등록해야 함
    }

    @Bean
    public Calculator calculator() {
        return new RecCalculator();
    }
}
```

### 6. 프록시 (Proxy) - AOP의 동작 원리

```java
Calculator cal = ctx.getBean("calculator", Calculator.class);
System.out.println(cal.getClass().getName());
// 출력: jdk.proxy2.$Proxy19 같은 프록시 클래스  <-- 원래 클래스가 아님! 프록시!
// (정확한 클래스명은 JDK 버전마다 다름. "프록시가 출력된다"는 점이 핵심)
```

**원리:**
```
[클라이언트] --> [프록시 객체] --> [실제 객체(RecCalculator)]
                    |
              여기서 Aspect의
              Advice가 실행됨
```

스프링은 AOP 적용 대상 Bean에 대해 **프록시 객체**를 자동 생성한다.
- `getBean()`으로 받는 것은 프록시
- 프록시가 Advice(공통 기능)를 실행하고, 그 안에서 실제 객체의 메서드를 호출

### 7. @Order - Aspect 실행 순서

Aspect가 여러 개일 때 순서를 정할 수 있다.

```java
@Aspect
@Order(1)                          // 숫자가 작을수록 먼저 실행
public class ExeTimeAspect { ... }

@Aspect
@Order(2)
public class CacheAspect { ... }
```

**실행 순서:**
```
요청 --> [ExeTimeAspect(1)] --> [CacheAspect(2)] --> [실제 메서드]
                                                          |
반환 <-- [ExeTimeAspect(1)] <-- [CacheAspect(2)] <-- [결과]
```

양파 껍질처럼 감싸는 구조! Order 숫자가 작은 게 바깥쪽.

### 8. 실전 예제: CacheAspect

```java
@Aspect
@Order(2)
public class CacheAspect {
    private Map<Long, Object> cache = new HashMap<>();

    @Around("execution(public * chapter07..*(long))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Long num = (Long) joinPoint.getArgs()[0];    // 첫 번째 인자

        if (cache.containsKey(num)) {
            System.out.println("Cache에서 구함: " + num);
            return cache.get(num);                    // 캐시 히트 -> 메서드 실행 안 함!
        }

        Object result = joinPoint.proceed();          // 캐시 미스 -> 실제 메서드 실행
        cache.put(num, result);                       // 결과를 캐시에 저장
        System.out.println("Cache에 추가: " + num);
        return result;
    }
}
```

**실행 결과:**
```
calculator.factorial(7)  -->  ExeTimeAspect 시작
                              CacheAspect: 캐시 미스 -> 실제 실행
                              CacheAspect: Cache에 추가 7
                              ExeTimeAspect: 실행시간 = 12345 ns

calculator.factorial(7)  -->  ExeTimeAspect 시작
                              CacheAspect: Cache에서 구함 7  (실제 실행 안 함!)
                              ExeTimeAspect: 실행시간 = 123 ns  (매우 빠름)
```

---

## Spring Boot와 뭐가 다른가 / 생소한 부분

| Spring Boot에서는 | 이 챕터에서는 |
|---|---|
| `spring-boot-starter-aop` 의존성 추가하면 자동 설정 | `@EnableAspectJAutoProxy`를 직접 붙여야 함 |
| `@Aspect` + `@Component`면 자동 등록 | Aspect를 `@Bean`으로 수동 등록해야 함 |
| AOP 내부 동작을 몰라도 사용 가능 | 프록시 원리를 직접 이해해야 함 |

**생소할 수 있는 부분:**
- `execution` 표현식 문법
- `ProceedingJoinPoint`와 `proceed()` 개념
- 프록시 패턴 (getBean의 결과가 원래 클래스가 아님)
- `@Order`에 의한 Aspect 중첩 실행 순서

---

## 시험 출제 포인트

### 객관식/단답형

1. **AOP의 핵심 목적은?** -> 공통 관심사(횡단 관심사)를 핵심 로직에서 분리
2. **Aspect, Advice, Pointcut, JoinPoint, Weaving 각각의 의미는?** -> 위 용어 표 참고
3. **@Around 어드바이스에서 실제 메서드를 호출하는 코드는?** -> `joinPoint.proceed()`
4. **@EnableAspectJAutoProxy의 역할은?** -> AOP 프록시 자동 생성을 활성화
5. **@Order(1)과 @Order(2) 중 먼저 실행되는 것은?** -> @Order(1) (숫자가 작은 것)
6. **execution(public * chapter07..*(..))에서 ".."의 의미는?** -> 하위 패키지 포함
7. **AOP 적용 후 getBean()으로 받는 객체의 실제 타입은?** -> 프록시 객체

### 서술형

1. **AOP가 필요한 이유를 데코레이터 패턴과 비교하여 설명하시오**
   - 데코레이터 패턴: 클래스마다 래퍼를 만들어야 함, 적용 대상이 늘면 클래스도 증가
   - AOP: Pointcut 표현식 하나로 여러 클래스/메서드에 일괄 적용, 코드 중복 제거

2. **@Around 어드바이스의 동작 흐름을 설명하시오**
   1. 대상 메서드 호출 시 프록시가 가로챔
   2. @Around 메서드 실행 (전처리)
   3. `joinPoint.proceed()`로 실제 메서드 호출
   4. @Around 메서드의 나머지 실행 (후처리)
   5. 결과 반환

3. **Aspect 실행 순서(@Order)와 프록시 구조를 설명하시오**
   - @Order 값이 작을수록 바깥쪽 프록시
   - 요청: 바깥 -> 안쪽 순서로 Advice 실행
   - 응답: 안쪽 -> 바깥 순서로 돌아옴

---

## 이해도 체크리스트

- [ ] AOP의 5가지 핵심 용어(Aspect, Advice, Pointcut, JoinPoint, Weaving)를 각각 설명할 수 있다
- [ ] @Aspect, @Around, @EnableAspectJAutoProxy의 역할을 안다
- [ ] execution 표현식을 읽고 "어떤 메서드에 적용되는지" 해석할 수 있다
- [ ] ProceedingJoinPoint의 proceed()가 뭔지 안다
- [ ] 프록시가 뭔지, 왜 getBean()의 결과가 원래 클래스가 아닌지 설명할 수 있다
- [ ] @Order의 숫자와 실행 순서의 관계를 안다
- [ ] 데코레이터 패턴 vs AOP의 장단점을 비교할 수 있다

---

## 검토 보강 포인트

- 실제 코드에는 `@Around("execution(...)")`를 바로 쓰는 방식뿐 아니라, `@Pointcut`으로 표현식을 따로 빼고 `@Around("publicTarget()")`처럼 **재사용하는 방식**도 나온다. 시험에서 둘 다 이해하고 있어야 한다.
- **Spring AOP의 JoinPoint는 사실상 메서드 실행 지점**이라고 생각하면 된다. 이 예제도 전부 메서드 실행 시점을 가로채는 형태다.
- `joinPoint.proceed()`는 "원래 메서드를 실제로 실행"하는 호출이다. **이걸 호출하지 않으면 대상 메서드는 실행되지 않는다.** CacheAspect가 캐시 히트 시 `proceed()`를 건너뛰는 이유가 바로 이것이다.
- 프록시 클래스명은 JDK 버전에 따라 다르다 (`com.sun.proxy.$ProxyN`, `jdk.proxy2.$ProxyN` 등). 특정 이름을 외울 필요는 없고, **원래 클래스가 아닌 프록시가 나온다**는 점이 핵심이다. **인터페이스 기반이면 JDK 동적 프록시, 클래스 기반이면 CGLIB 프록시**가 사용될 수 있다. 현재 예제는 `Calculator` 인터페이스가 있어서 프록시 개념을 보기 쉽다.
- **자기 자신을 내부에서 다시 호출하는 self-invocation은 프록시를 거치지 않는다.** 따라서 `RecCalculator.factorial()`의 재귀 호출은 AOP가 매번 다시 적용되는 구조가 아니라는 점을 알아두면 이해가 깊어진다.
- `@Order`는 숫자가 작을수록 먼저 실행되지만, 구조적으로는 **더 바깥쪽에서 감싼다**고 이해하면 된다. 그래서 요청 시에는 작은 숫자가 먼저, 반환 시에는 작은 숫자가 나중에 빠져나온다.
- AOP 예제를 실행하려면 설정 외에도 `aspectjweaver` 같은 **AOP 관련 의존성**이 필요하다는 점도 함께 기억해두면 좋다.
