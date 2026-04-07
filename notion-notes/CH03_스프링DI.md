# CH03. 스프링 DI (의존 주입)

---

## 이 장의 핵심 한 줄 요약

> **객체가 필요한 다른 객체(의존)를 직접 만들지 않고, 외부(스프링)에서 넣어주는 것이 DI(Dependency Injection)이다.**

---

## 하고 싶은 말이 뭔데?

### 문제 상황: 직접 조립하면 뭐가 불편한가?

```java
// Assembler.java - 수동 조립
public class Assembler {
    private MemberDao memberDao;
    private MemberRegisterService regSvc;
    private ChangePasswordService pwdSvc;

    public Assembler() {
        memberDao = new MemberDao();
        regSvc = new MemberRegisterService(memberDao);     // 생성자로 주입
        pwdSvc = new ChangePasswordService();
        pwdSvc.setMemberDao(memberDao);                     // setter로 주입
    }
}
```

이것도 DI다! 하지만 문제는:
- 객체가 10개, 20개 되면? -> 조립 코드가 엄청 복잡해짐
- DAO를 바꾸고 싶으면? -> Assembler 코드를 직접 수정해야 함

### 해결: 스프링이 대신 조립해준다

```java
@Configuration
public class AppContext {
    @Bean
    public MemberDao memberDao() {
        return new MemberDao();
    }

    @Bean
    public MemberRegisterService memberRegisterService() {
        // 생성자 주입: memberDao()를 인자로 넘김
        return new MemberRegisterService(memberDao());
    }

    @Bean
    public ChangePasswordService changePasswordService() {
        ChangePasswordService svc = new ChangePasswordService();
        svc.setMemberDao(memberDao());    // setter 주입
        return svc;
    }
}
```

---

## 반드시 알아야 할 핵심 개념

### 1. 의존(Dependency)이란?

**"A가 B를 사용한다" = "A가 B에 의존한다"**

```java
public class MemberRegisterService {
    private MemberDao memberDao;   // MemberDao에 의존!

    // memberDao 없으면 회원 등록 못함 = 의존
    public void regist(RegisterRequest req) {
        Member member = memberDao.selectByEmail(req.getEmail());
        // ...
    }
}
```

### 2. DI (Dependency Injection) = 의존 주입

의존 객체를 **직접 생성하지 않고**, **외부에서 받는** 것

| 방식 | 코드 | 설명 |
|------|------|------|
| **생성자 주입** | `new Service(dao)` | 생성할 때 넣어줌 (가장 권장) |
| **setter 주입** | `svc.setDao(dao)` | 만든 후에 넣어줌 |

```java
// 생성자 주입
public class MemberRegisterService {
    private MemberDao memberDao;

    public MemberRegisterService(MemberDao memberDao) {
        this.memberDao = memberDao;   // 외부에서 받음!
    }
}

// setter 주입
public class ChangePasswordService {
    private MemberDao memberDao;

    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;   // 외부에서 받음!
    }
}
```

**비교 - DI 안 하면 (나쁜 예):**
```java
public class MemberRegisterService {
    private MemberDao memberDao = new MemberDao();  // 직접 생성 = DI 아님!
}
```
이러면 MemberDao를 바꿀 수 없다. 테스트도 어렵다.

### 3. 설정 클래스에서의 의존 주입

```java
@Configuration
public class AppContext {

    @Bean
    public MemberDao memberDao() {
        return new MemberDao();
    }

    @Bean
    public MemberRegisterService memberRegisterService() {
        return new MemberRegisterService(memberDao());
        //                               ^^^^^^^^^^^
        //          memberDao() 호출 = 스프링이 관리하는 싱글톤 Bean을 가져옴
        //          new MemberDao()가 아님! (중요)
    }
}
```

**주의:** `@Configuration` 클래스 안에서 `memberDao()`를 여러 번 호출해도 **같은 객체**를 리턴한다. 스프링이 프록시를 통해 싱글톤을 보장하기 때문!

### 4. 설정 분리와 @Import

설정이 커지면 파일을 나눌 수 있다:

```java
// AppConf1.java - DAO, Printer 관련 Bean
@Configuration
public class AppConf1 {
    @Bean public MemberDao memberDao() { ... }
    @Bean public MemberPrinter memberPrinter() { ... }
}

// AppConf2.java - Service 관련 Bean (AppConf1의 Bean을 @Autowired로 주입받음)
@Configuration
public class AppConf2 {
    @Autowired private MemberDao memberDao;
    @Autowired private MemberPrinter memberPrinter;

    @Bean public MemberRegisterService memberRegisterService() { ... }
}

// AppConfImport.java - 합치기
@Configuration
@Import(AppConf2.class)        // AppConf2를 가져옴
public class AppConfImport {
    @Bean public MemberDao memberDao() { ... }
    @Bean public MemberPrinter memberPrinter() { ... }
}
```

| 어노테이션 | 역할 |
|---|---|
| `@Import(설정클래스.class)` | 다른 설정 클래스를 현재 설정에 포함 |
| `@Import({A.class, B.class})` | 여러 설정 클래스를 한번에 포함 |

---

## 이 장의 전체 구조 한눈에 보기

```
[Main 클래스]
    |
    v
[스프링 컨테이너] <-- AppContext.class (@Configuration)
    |
    +-- MemberDao (Bean)
    |       ^
    |       |  (의존 주입)
    +-- MemberRegisterService (Bean) --- 생성자 주입으로 MemberDao 받음
    |       ^
    |       |  (의존 주입)
    +-- ChangePasswordService (Bean) --- setter 주입으로 MemberDao 받음
    |
    +-- MemberPrinter (Bean)
    |       ^
    |       |  (의존 주입)
    +-- MemberListPrinter (Bean) --- 생성자 주입으로 MemberDao + MemberPrinter 받음
    |
    +-- MemberInfoPrinter (Bean) --- setter 주입으로 MemberDao + MemberPrinter 받음
```

---

## Spring Boot와 뭐가 다른가 / 생소한 부분

| Spring Boot에서는 | 이 챕터에서는 |
|---|---|
| `@Autowired` 붙이면 자동 주입 | `@Bean` 메서드 안에서 직접 연결 |
| Bean 등록이 자동 (`@Component` 스캔) | `@Bean` 메서드로 **하나하나 수동 등록** |
| 설정 파일 거의 안 봄 | 설정 클래스가 핵심 |

**생소할 수 있는 부분:**
- `Assembler` 클래스: 스프링 없이 DI하는 방법. 스프링이 왜 필요한지 이해하기 위한 **비교 대상**
- `@Configuration` 안에서 `@Bean` 메서드를 호출하면 new가 아니라 싱글톤 Bean을 가져온다는 점

---

## 시험 출제 포인트

### 객관식/단답형

1. **DI란?** -> Dependency Injection (의존 주입). 객체가 사용할 의존 객체를 외부에서 주입받는 것
2. **생성자 주입 vs setter 주입의 차이는?**
   - 생성자 주입: 객체 생성 시점에 모든 의존 설정, 불변 보장
   - setter 주입: 객체 생성 후 필요 시점에 의존 설정, 선택적 의존에 적합
3. **@Configuration 클래스 안에서 @Bean 메서드를 여러 번 호출하면?** -> 같은 싱글톤 객체를 리턴
4. **@Import의 역할은?** -> 다른 @Configuration 클래스를 현재 설정에 포함시킴
5. **DI를 사용하는 이유는?** -> 결합도를 낮추고, 변경 용이성과 테스트 용이성을 높이기 위해

### 서술형

1. **DI와 직접 객체 생성의 차이를 코드 예시와 함께 설명하시오**

   DI 사용:
   ```java
   public class Service {
       private Dao dao;
       public Service(Dao dao) { this.dao = dao; }  // 외부에서 주입
   }
   ```
   직접 생성:
   ```java
   public class Service {
       private Dao dao = new Dao();  // 내부에서 직접 생성
   }
   ```
   DI를 사용하면 Dao 구현체를 바꿀 때 Service 코드를 수정하지 않아도 되고, 테스트 시 Mock 객체를 넣을 수 있다.

2. **Assembler 패턴과 스프링 컨테이너의 공통점과 차이점을 설명하시오**
   - 공통점: 둘 다 객체를 생성하고 의존 관계를 연결해준다 (조립 역할)
   - 차이점: Assembler는 개발자가 직접 코드를 작성, 스프링은 설정(@Configuration)만 해주면 자동으로 관리. 스프링은 싱글톤 보장, 라이프사이클 관리 등 추가 기능 제공

---

## 이해도 체크리스트

- [ ] "의존"이 뭔지 한 문장으로 설명할 수 있다
- [ ] 생성자 주입과 setter 주입의 차이를 코드로 보여줄 수 있다
- [ ] `@Bean` 메서드 안에서 다른 `@Bean` 메서드를 호출하면 왜 싱글톤인지 안다
- [ ] DI를 안 하면(직접 new) 뭐가 문제인지 설명할 수 있다
- [ ] `@Import`가 왜 필요하고 어떻게 쓰는지 안다
- [ ] Assembler 클래스의 역할과 스프링 컨테이너의 관계를 설명할 수 있다

---

## 검토 보강 포인트

- `Assembler`도 사실은 **스프링 없이 DI를 구현한 예**다. 즉, CH03의 핵심은 "DI 자체"이고, 스프링은 그 조립을 더 편하게 해주는 도구라는 점을 기억하면 된다.
- 이 장의 실제 코드에서 **필수 의존성은 생성자 주입**으로, 선택적이거나 나중에 바꿀 수 있는 의존성은 **setter 주입**으로 보여준다. 시험에서는 보통 생성자 주입이 더 안전하고 권장된다고 정리하면 된다.
- `@Configuration` 클래스 안에서 다른 `@Bean` 메서드를 호출해도 같은 Bean이 나오는 이유는 **스프링이 설정 클래스를 프록시로 처리해 싱글톤을 보장하기 때문**이다. 그냥 일반 클래스 메서드 호출과 같다고 생각하면 안 된다.
- 실제 코드의 [AppConf2.java](D:/Spring/CH03/src/main/java/org/example/chapter03/AppConf2.java)는 **설정 클래스도 Bean을 주입받을 수 있다**는 점을 보여준다. 즉, `@Autowired`는 서비스 클래스뿐 아니라 설정 클래스에서도 사용할 수 있다.
- "객체가 객체를 참조하는 의존 관계"와 "스프링이 Bean을 연결하는 참조 관계"를 같이 떠올리면 이해가 쉽다. 코드에서는 `MemberRegisterService -> MemberDao` 의존을 스프링이 Bean 연결로 해결한다.
