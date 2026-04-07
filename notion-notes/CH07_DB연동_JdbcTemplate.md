# CH07. DB 연동 (JDBC, JdbcTemplate, 트랜잭션)

> **참고:** 실습 코드는 `CH07` 폴더에 있지만, 패키지명은 교재 원본을 따라 `chapter08`로 되어 있다. 실행에는 문제없음.

---

## 이 장의 핵심 한 줄 요약

> **스프링은 JDBC의 반복 코드(Connection, Statement, ResultSet 관리)를 JdbcTemplate으로 줄여주고, @Transactional로 트랜잭션을 자동 관리해준다.**

---

## 하고 싶은 말이 뭔데?

### 문제 상황: 순수 JDBC는 너무 길고 반복된다

```java
// DbQuery.java - 순수 JDBC로 count() 하나 하려면...
public int count() {
    Connection conn = null;
    try {
        conn = dataSource.getConnection();           // 1) 커넥션 얻기
        try (
            Statement stmt = conn.createStatement();  // 2) Statement 생성
            ResultSet rs = stmt.executeQuery("select count(*) from MEMBER")  // 3) SQL 실행
        ) {
            rs.next();
            return rs.getInt(1);                      // 4) 결과 꺼내기
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);                // 5) 예외 처리
    } finally {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) {}  // 6) 자원 정리
        }
    }
}
```

**SQL 한 줄 실행하는데 코드가 20줄!** Connection, Statement, ResultSet 열고 닫고... 메서드 10개면 이 코드가 10번 반복된다.

### 해결: JdbcTemplate으로 한 줄로!

```java
// MemberDao.java - JdbcTemplate 사용
public int count() {
    Integer count = jdbcTemplate.queryForObject("select count(*) from MEMBER", Integer.class);
    return count;
}
```

**Connection 열고 닫기, 예외 처리 전부 JdbcTemplate이 알아서 해준다!**

---

## 반드시 알아야 할 핵심 개념

### 1. DataSource와 커넥션 풀

`DataSource`는 DB 커넥션을 제공하는 **표준 인터페이스**(`javax.sql.DataSource`)다. 그 자체가 커넥션 풀은 아니지만, 이 예제에서는 **Tomcat JDBC DataSource(커넥션 풀 구현체)** 를 사용한다.

**커넥션 풀이란?** DB 커넥션을 매번 새로 만들면 느리니까 **미리 여러 개 만들어놓고 빌려 쓰는 것**

```java
@Bean(destroyMethod = "close")
public DataSource dataSource() {
    DataSource ds = new DataSource();       // Tomcat JDBC 커넥션 풀
    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");  // MySQL 드라이버
    ds.setUrl("jdbc:mysql://localhost:3307/daelim?characterEncoding=utf8&serverTimezone=Asia/Seoul");
    ds.setUsername("spring");
    ds.setPassword("daelimspring");

    // 커넥션 풀 설정
    ds.setInitialSize(2);     // 초기 커넥션 수
    ds.setMaxActive(10);      // 최대 활성 커넥션 수
    ds.setMaxIdle(10);        // 최대 유휴 커넥션 수

    // 커넥션 검증 설정
    ds.setTestWhileIdle(true);                          // 유휴 상태일 때 검증
    ds.setTimeBetweenEvictionRunsMillis(1000 * 10);     // 검증 주기 (10초)
    ds.setMinEvictableIdleTimeMillis(1000 * 60 * 3);    // 최소 유휴 시간 (3분)
    ds.setValidationQuery("select 1");                  // 검증 쿼리
    ds.setValidationQueryTimeout(3);                    // 검증 타임아웃 (3초)
    return ds;
}
```

| 설정 | 의미 |
|------|------|
| `initialSize` | 풀 생성 시 초기 커넥션 수 |
| `maxActive` | 동시에 사용할 수 있는 최대 커넥션 수 |
| `maxIdle` | 풀에 유휴 상태로 남겨둘 최대 커넥션 수 |
| `testWhileIdle` | 유휴 커넥션이 유효한지 주기적으로 검사 |
| `validationQuery` | 커넥션 유효 여부 검사에 사용할 쿼리 |

**왜 커넥션 풀이 필요한가?**
- DB 커넥션 생성은 비용이 크다 (TCP 연결 + 인증)
- 매 요청마다 새로 만들면 성능 저하
- 미리 만들어두고 재사용 -> 빠르다!

### 2. JdbcTemplate - JDBC 코드를 간결하게

JdbcTemplate은 DataSource를 받아서 생성한다:

```java
public class MemberDao {
    private final JdbcTemplate jdbcTemplate;

    public MemberDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);  // DataSource로 생성
    }
}
```

#### (1) 조회 - query()

여러 행을 조회할 때:

```java
// selectAll() - 전체 조회
public List<Member> selectAll() {
    return jdbcTemplate.query(
        "select * from MEMBER",        // SQL
        new MemberRowMapper()          // RowMapper (결과 매핑)
    );
}

// selectByEmail() - 조건 조회
public Member selectByEmail(String email) {
    List<Member> results = jdbcTemplate.query(
        "select * from MEMBER where EMAIL = ?",   // ? = 파라미터 바인딩
        new MemberRowMapper(),
        email                                      // ?에 들어갈 값
    );
    return results.isEmpty() ? null : results.get(0);
}
```

#### (2) 단일 값 조회 - queryForObject()

```java
public int count() {
    Integer count = jdbcTemplate.queryForObject(
        "select count(*) from MEMBER",    // SQL
        Integer.class                     // 결과 타입
    );
    return count;
}
```

#### (3) 수정 - update()

INSERT, UPDATE, DELETE에 사용:

```java
public void update(Member member) {
    jdbcTemplate.update(
        "update MEMBER set NAME = ?, PASSWORD = ? where EMAIL = ?",
        member.getName(), member.getPassword(), member.getEmail()    // ?에 들어갈 값들
    );
}
```

#### (4) 삽입 + 자동 생성 키 - insert with KeyHolder

```java
public void insert(Member member) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(new PreparedStatementCreator() {
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement pstmt = con.prepareStatement(
                "insert into MEMBER(EMAIL, PASSWORD, NAME, REGDATE) values (?,?,?,?)",
                new String[] {"ID"}      // ★ 자동 생성되는 키 컬럼명
            );
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setTimestamp(4, Timestamp.valueOf(member.getRegisterDateTime()));
            return pstmt;
        }
    }, keyHolder);

    Number keyValue = keyHolder.getKey();      // ★ DB가 생성한 ID 값을 가져옴
    member.setId(keyValue.longValue());
}
```

**KeyHolder 핵심:**
- `GeneratedKeyHolder`: DB가 AUTO_INCREMENT로 생성한 키 값을 담아주는 객체
- `new String[] {"ID"}`: 자동 생성 키의 컬럼명을 지정
- `keyHolder.getKey()`: INSERT 후 생성된 키 값을 꺼냄

### 3. RowMapper - ResultSet을 객체로 변환

```java
public class MemberRowMapper implements RowMapper<Member> {
    @Override
    public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
        Member member = new Member(
            rs.getString("EMAIL"),
            rs.getString("PASSWORD"),
            rs.getString("NAME"),
            rs.getTimestamp("REGDATE").toLocalDateTime()
        );
        member.setId(rs.getLong("ID"));
        return member;
    }
}
```

- `RowMapper<T>`: ResultSet의 **한 행**을 T 객체로 변환하는 인터페이스
- `mapRow()`: 각 행마다 호출됨. `rs`에서 컬럼 값을 꺼내 객체를 만들어 리턴
- JdbcTemplate의 `query()` 메서드가 내부적으로 반복 호출해서 `List<T>`를 만들어줌

### 4. @Transactional - 트랜잭션 관리

**트랜잭션이란?** 여러 DB 작업을 하나의 단위로 묶는 것. **전부 성공 아니면 전부 취소(롤백).**

```java
public class ChangePasswordService {
    @Autowired
    private MemberDao memberDao;

    @Transactional                    // ★ 이 메서드를 하나의 트랜잭션으로!
    public void changePassword(String email, String oldPw, String newPw) {
        Member member = memberDao.selectByEmail(email);   // 1) 조회
        if (member == null)
            throw new MemberNotFoundException();          // 예외 -> 롤백!

        member.changePassword(oldPw, newPw);              // 2) 비밀번호 변경
        memberDao.update(member);                         // 3) DB 반영
    }
    // 정상 종료 -> 커밋!  /  예외 발생 -> 롤백!
}
```

**@Transactional 동작 원리:**
```
메서드 시작 -> 트랜잭션 시작 (BEGIN)
    |
    +-- 정상 완료 -> 커밋 (COMMIT)
    |
    +-- RuntimeException 발생 -> 롤백 (ROLLBACK)
```

### 5. @EnableTransactionManagement + TransactionManager

트랜잭션을 사용하려면 설정이 필요하다:

```java
@Configuration
@EnableTransactionManagement          // ★ @Transactional 활성화!
public class AppConfig {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);  // ★ 트랜잭션 매니저
    }
}
```

| 설정 | 역할 |
|------|------|
| `@EnableTransactionManagement` | @Transactional 어노테이션 처리를 활성화 |
| `PlatformTransactionManager` | 트랜잭션 관리 인터페이스 |
| `DataSourceTransactionManager` | JDBC용 트랜잭션 매니저 구현체 |

**AOP와의 관계:** @Transactional도 내부적으로 **AOP 프록시**로 동작한다! CH06의 @Around처럼 메서드를 감싸서 트랜잭션을 시작/커밋/롤백한다.

### 6. 순수 JDBC vs JdbcTemplate 비교 (DbQuery vs MemberDao)

이 챕터의 코드에는 **같은 기능을 두 가지 방식**으로 구현한 예제가 있다:

| 비교 | DbQuery (순수 JDBC) | MemberDao (JdbcTemplate) |
|------|---------------------|--------------------------|
| 커넥션 관리 | 직접 `getConnection()`, `close()` | JdbcTemplate이 자동 처리 |
| 예외 처리 | `try-catch-finally` 직접 작성 | JdbcTemplate 내부에서 처리 |
| SQL 실행 | `Statement`, `PreparedStatement` 직접 | `query()`, `update()` 메서드 호출 |
| 코드량 | 많음 (20줄+) | 적음 (1~3줄) |
| 결과 매핑 | `ResultSet` 직접 처리 | `RowMapper`로 분리 |

---

## JdbcTemplate 메서드 정리표

| 메서드 | 용도 | 리턴 타입 | 예시 |
|--------|------|----------|------|
| `query(sql, RowMapper)` | 여러 행 조회 | `List<T>` | `selectAll()` |
| `query(sql, RowMapper, args...)` | 조건 조회 | `List<T>` | `selectByEmail()` |
| `queryForObject(sql, 타입.class)` | 단일 값 조회 | `T` | `count()` |
| `update(sql, args...)` | UPDATE/DELETE | `int` (변경 행 수) | `update()` |
| `update(PreparedStatementCreator, KeyHolder)` | INSERT + 자동 키 | `int` | `insert()` |

---

## Spring Boot와 뭐가 다른가 / 생소한 부분

| Spring Boot에서는 | 이 챕터에서는 |
|---|---|
| `application.properties`에 DB 정보 작성 | `@Bean`에서 DataSource 직접 설정 |
| `spring-boot-starter-jdbc` + DB 드라이버로 대부분 자동 설정 | `spring-jdbc` + `tomcat-jdbc` + `mysql-connector` 직접 추가 |
| `JdbcTemplate` 자동 생성 (Bean으로 등록됨) | DAO 클래스 안에서 `new JdbcTemplate(dataSource)` 직접 생성 |
| `@Transactional` 바로 사용 가능 | `@EnableTransactionManagement` + `TransactionManager` Bean 직접 등록 |
| HikariCP가 기본 커넥션 풀 | Tomcat JDBC 커넥션 풀 사용 |

**생소할 수 있는 부분:**
- `DataSource`를 Bean으로 직접 만들고 커넥션 풀 옵션을 하나하나 설정하는 것
- `PreparedStatementCreator`와 `KeyHolder` 조합 (insert 코드가 길어지는 이유)
- `RowMapper` 인터페이스를 구현해서 ResultSet -> 객체 매핑을 직접 하는 것
- `@Transactional`이 AOP 프록시로 동작한다는 점

---

## 시험 출제 포인트

### 객관식/단답형

1. **JdbcTemplate을 생성할 때 필요한 것은?** -> DataSource
2. **여러 행을 조회하는 JdbcTemplate 메서드는?** -> `query()`
3. **단일 값(count 등)을 조회하는 메서드는?** -> `queryForObject()`
4. **INSERT 후 자동 생성된 키 값을 받으려면?** -> `KeyHolder` (`GeneratedKeyHolder`)
5. **ResultSet의 한 행을 객체로 변환하는 인터페이스는?** -> `RowMapper`
6. **@Transactional 메서드에서 RuntimeException이 발생하면?** -> 롤백(ROLLBACK)
7. **@Transactional을 활성화하려면 설정 클래스에 뭘 붙여야 하나?** -> `@EnableTransactionManagement`
8. **JDBC 트랜잭션 매니저 구현체는?** -> `DataSourceTransactionManager`
9. **커넥션 풀을 사용하는 이유는?** -> 커넥션 생성 비용이 크므로 미리 만들어 재사용하여 성능 향상
10. **@Transactional은 내부적으로 어떤 방식으로 동작하나?** -> AOP 프록시

### 서술형

1. **순수 JDBC와 JdbcTemplate의 차이를 설명하시오**
   - 순수 JDBC: Connection 획득, Statement 생성, SQL 실행, ResultSet 처리, 예외 처리, 자원 해제를 모두 개발자가 직접 코드로 작성해야 한다
   - JdbcTemplate: 위의 반복적인 코드를 내부에서 처리하고, 개발자는 SQL과 결과 매핑(RowMapper)만 작성하면 된다
   - 결과: 코드량이 대폭 줄고, 자원 해제 누락 같은 실수를 방지

2. **@Transactional의 동작 원리를 AOP와 연관 지어 설명하시오**
   - @Transactional이 붙은 메서드는 스프링이 AOP 프록시로 감싼다
   - 프록시가 메서드 호출 전에 트랜잭션을 시작(BEGIN)하고
   - 정상 완료 시 커밋(COMMIT), RuntimeException 발생 시 롤백(ROLLBACK)한다
   - CH06의 @Around 어드바이스와 비슷한 구조

3. **KeyHolder와 PreparedStatementCreator의 역할을 설명하시오**
   - `PreparedStatementCreator`: SQL과 파라미터 바인딩을 담은 PreparedStatement를 생성하는 역할. 자동 생성 키 컬럼을 지정할 수 있다
   - `KeyHolder(GeneratedKeyHolder)`: INSERT 실행 후 DB가 자동 생성한 키(AUTO_INCREMENT) 값을 담아서 돌려주는 역할
   - 이 둘을 `jdbcTemplate.update()`에 함께 전달하면 INSERT + 생성된 ID 확보가 가능

---

## 이해도 체크리스트

- [ ] DataSource가 DB 커넥션 제공 인터페이스라는 것, 이 예제에서는 Tomcat JDBC 커넥션 풀을 사용한다는 것을 안다
- [ ] JdbcTemplate을 DataSource로 생성한다는 걸 안다
- [ ] `query()`, `queryForObject()`, `update()`의 용도를 구분할 수 있다
- [ ] RowMapper의 역할과 mapRow() 메서드가 하는 일을 설명할 수 있다
- [ ] KeyHolder + PreparedStatementCreator로 INSERT + 자동 키 확보하는 코드를 이해한다
- [ ] 순수 JDBC(DbQuery)와 JdbcTemplate(MemberDao)의 차이를 비교할 수 있다
- [ ] @Transactional이 뭔지, 언제 커밋/롤백되는지 안다
- [ ] @EnableTransactionManagement + DataSourceTransactionManager가 왜 필요한지 안다
- [ ] @Transactional이 AOP 프록시로 동작한다는 걸 안다

---

## 검토 보강 포인트

- `JdbcTemplate`은 직접 `new`로 생성하지만, 그 안에서 쓰는 `DataSource`는 스프링 Bean이다. 즉, JdbcTemplate 자체가 Bean일 필요는 없고 **DataSource만 Bean으로 관리**하면 된다.
- `@Transactional`은 **RuntimeException(Unchecked Exception)에만 기본 롤백**한다. `Exception`(Checked Exception)이 발생하면 기본적으로 커밋된다. 이 점은 시험에서 함정 문제로 나올 수 있다.
- `@Transactional`도 **AOP 프록시**로 동작하므로, CH06에서 배운 것처럼 `getBean()`으로 받은 객체는 프록시다. 같은 클래스 내부에서 자기 자신의 `@Transactional` 메서드를 호출하면 프록시를 거치지 않아 트랜잭션이 적용되지 않는다 (self-invocation 문제).
- 코드에서 `destroyMethod = "close"`는 스프링 컨테이너가 종료될 때 DataSource의 `close()` 메서드를 호출해서 **커넥션 풀의 모든 커넥션을 정리**하라는 설정이다.
- `DbQuery` 클래스는 순수 JDBC의 번거로움을 보여주기 위한 **비교 대상**이다. 실제 프로젝트에서는 JdbcTemplate을 사용한다. Assembler(CH03)와 같은 역할의 예제라고 생각하면 된다.
- 이 장부터 Docker(docker-compose)로 MySQL을 띄우는데, 시험과는 무관하지만 실습 환경 이해를 위해 `docker-compose.yml`의 포트(3307), DB명(daelim), 계정(spring/daelimspring)을 알아두면 좋다.
- `queryForObject()`는 결과가 없으면 `EmptyResultDataAccessException`이 발생한다. `query()`는 결과가 없으면 빈 리스트를 반환한다. 이 차이도 알아두면 좋다.
