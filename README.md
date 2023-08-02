# 멋쟁이사자 백엔드 스쿨 5기 ♻️멋사마켓 미션형[1] 프로젝트

```

💡 여러분들이 많이 사용하고 있는 🥕당근마켓, 중고나라 등을 착안하여 여러분들만의 중고 제품 거래 플랫폼을 만들어보는 미니 프로젝트입니다.

사용자가 중고 물품을 자유롭게 올리고, 댓글을 통해 소통하며, 최종적으로 구매 제안에 대하여 수락할 수 있는 형태의 중고 거래 플랫폼의 백엔드를 만들어봅시다.

```

#### 기존 멋쟁이사자 백엔드 스쿨 5기 미니 프로젝트를 고도화를 하였다. (기존 프젝 링크 첨부)

```
https://github.com/likelion-backend-5th/MiniProject_Basic_BaeJeWoo
```

```
1~3일차 요구사항은 전부 POSTMAN으로 ProjectTest가 가능합니다.
POSTMAN export 한 파일 이름: Project Test.postman_collection.json
파일 안에 넣었습니다! 
```
#### DAY 1 요구사항 / 인증 만들기

```
💡 본래 만들었던 서비스에 사용자 인증을 첨부합니다.
1. 사용자는 회원가입을 진행할 수 있다.
    - 회원가입에 필요한 정보는 아이디와 비밀번호가 필수이다.
    - 부수적으로 전화번호, 이메일, 주소 정보를 기입할 수 있다.
    - 이에 필요한 사용자 Entity는 직접 작성하도록 한다.

2. 아이디 비밀번호를 통해 로그인을 할 수 있어야 한다.

3. 아이디 비밀번호를 통해 로그인에 성공하면, JWT가 발급된다. 이 JWT를 소유하고 있을 경우 인증이 필요한 서비스에 
   접근이 가능해 진다.
   - 인증이 필요한 서비스는 추후(미션 후반부) 정의한다.

4. JWT를 받은 서비스는 사용자가 누구인지 사용자 Entity를 기준으로 정확하게 판단할 수 있어야 한다.
```

#### 1. 인증 만들기 설명
```
1. 사용자는 회원가입을 진행할 수 있다.

1) TokenController에서 /token/register로 회원가입 가능하다.   
UserEntity 에서 @Column(nullable = false, unique = true) 옵션으로 아이디와 비밀번호를 받아줬다.

2) 전화번호, 이메일, 주소 정보를 추가적으로 기입할 수 있게 코드를 추가하였다.
UserEntity ex) private String address;

JpaUserDetails createUser 메서드 
createUser(CustomUserDetails.builder().address(dto.getAddress()).email(dto.getEmail())......

2. 아이디 비밀번호를 통해 로그인을 할 수 있어야 한다. 
TokenController 에서 token/issue 요청으로 로그인이 가능하다.
JwtRequestDto로 username , password를 받는다. 
UserDetailsManger의 구현 클레스인 JpaUsersDetailsManager에 있는 
loadUserByUsername 메서드로 사용자 인증을 한다.
passwordEncoder.matches 를 사용해서 받아온 password와 userDetils(등록된 사용자)에 있는 password를 비교한다
notmatches -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED)

인증 과정을 통과하면 jwtTokenUtils클레스의 generateToken 메서드로 jwt token을 발행하여 응답한다.

3. 아이디 비밀번호를 통해 로그인에 성공하면, JWT가 발급된다. 이 JWT를 소유하고 있을 경우 인증이 필요한 서비스에 
   접근이 가능해 진다.

로그인, 회원가입 url 과 물품조회, 댓글조회 url 를 제외한 모든 경로는 JWT를 소유해야 접근이 가능하다.
-> WebSecurityConfig에서 확인 가능하다. 

4. JWT를 받은 서비스는 사용자가 누구인지 사용자 Entity를 기준으로 정확하게 판단할 수 있어야 한다.

-> token/check 요청으로 현재 사용자(username)가 누구인지 확인 가능하게 만들었다.

```


#### DAY 2 요구사항 / 관계 설정하기

```
💡 이전에 만든 물품, 댓글들에 대한 데이터베이스 테이블을, 사용자 정보를 포함하여 고도화 합니다.

 1. 아이디와 비밀번호를 필요로 했던 테이블들은 실제 사용자 Record에 대응되도록 ERD를 수정하자.
    - ERD 수정과 함께 해당 정보를 적당히 표현할 수 있도록 Entity를 재작성하자.
    - 그리고 ORM의 기능을 충실히 사용할 수 있도록 어노테이션을 활용한다.
 2. 다른 작성한 Entity도 변경을 진행한다.
    - 서로 참조하고 있는 테이블 관계가 있다면, 해당 사항이 표현될 수 있도록 Entity를 재작성한다.  

```

#### 2. 관계 설정하기 설명
```
관계설정은 UserEntiy(사용자) , MarketEntity(물품) , NegotiationEntity(제안), CommentEntity(댓글) 에서 확인 가능하다.

ERD 설명

 1 :  N
User -> Market(item), Comment, Negotiation

 1 :  N
Market(item) -> Comment, Negotiation

관계 설정 설명
@OneToMany 와 @ManyToOne 어노테이션을 사용해서 양방향으로 연결하였다.
옵션은 mappedBy를 사용하였다. 
mappedBy 옵션은 양방향 연관관계에서 사용한다.
ex) Market(item) -> Comment

@OneToMany(mappedBy = "salesItem")
private List<CommentEntity> commentEntityList = new ArrayList<>();

여기서 관계의 주인은 item 이다. 그럼 comment 엔티티는 salesItem을 참조 갖는다.

```

#### DAY 3 요구사항 / 기능 접근 설정하기

```
💡 기능들의 사용 가능 여부가 사용자의 인증 상태에 따라 변동하도록 제작합니다.

 1. 본래 “누구든지 열람할 수 있다”의 기능 목록은 사용자가 인증하지 않은 상태에서 사용할 수 있도록 한다.
    - 등록된 물품 정보는 누구든지 열람할 수 있다.
    - 등록된 댓글은 누구든지 열람할 수 있다.
    - 기타 기능들

 2. 작성자와 비밀번호를 포함하는 데이터는 인증된 사용자만 사용할 수 있도록 한다.
    - 이때 해당하는 기능에 포함되는 아이디 비밀번호 정보는, 1일차에 새로 작성한 사용자 Entity와의 관계로 대체한다.
        - 물품 정보 등록 → 물품 정보와 사용자 관계 설정
        - 댓글 등록 → 댓글과 사용자 관계 설정
        - 기타 등등
        - 누구든지 중고 거래를 목적으로 물품에 대한 정보를 등록할 수 있다.
        - 등록된 물품에 대한 질문을 위하여 댓글을 등록할 수 있다.
        - 등록된 물품에 대하여 구매 제안을 등록할 수 있다.
        - 기타 기능들
```


#### 3. 기능 접근 설정하기 설명

```
1. 누구든지 열람할 수 있다. -> 인증되지 않은 상태에서 사용할 수 있도록 한다.
물품정보 전체조회, 단일조회, 댓글 전체조회
-> webSecurityConfig 
.requestMatchers(HttpMethod.GET, "/items", "/items/{itemId}", "/items/{itemId}/comments").anonymous()

2. 작성자와 비밀번호를 포함하는 데이터는 인증된 사용자만 사용할 수 있도록 한다.
1) market(item), comment, negotiation : 등록(post) 
모두 현재의 사용자 정보를 가져와서 entity에 관계 설정을 해주었다.
ex) 물품 등록 
 public MarketDto createMarket(MarketDto dto, Authentication authentication){
 Optional<UserEntity> optionalUser = userRepository.findByUsername(currentUser);
        UserEntity user = optionalUser.get();
        MarketEntity marketEntity = new MarketEntity();
        marketEntity.setStatus("판매중");
        marketEntity.setTitle(dto.getTitle());
        marketEntity.setDescription(dto.getDescription());
        marketEntity.setMin_price_wanted(dto.getMin_price_wanted());
        marketEntity.setUser(user);
 return MarketDto.fromEntity(marketRepository.save(marketEntity));
 }

 2) market(item), comment, negotiation : 업데이트(put), 삭제(delete)
 모두 현재의 사용자 정보와 기존에 등록했던 사용자의 정보를 비교해서 동작하게 만들었다.
ex) 물품 정보 삭제 
public void deleteMarket(Long id, Authentication authentication){
        Optional<MarketEntity> optionalMarket = marketRepository.findById(id);
        if (optionalMarket.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        MarketEntity marketEntity = optionalMarket.get();
        if (authentication.getName().equals(marketEntity.getUser().getUsername())) {
            marketRepository.deleteById(id);
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  요구사항에 나와 있는 기능들을 전부 넣어주었다.
  MarketController, MarketService, CommentController, CommentService, NegotiationController, 
  NegotiationService 코드를 참고 해주세요!

```

#### DAY 4 요구사항 / UI 구현하기

```
💡 각 기능을 활용할 수 있는 적절한 UI를 만들어봅시다

 1. 회원가입 화면을 구성하기 위해 필요한 항목을 생각해보자.
    - 아이디
    - 비밀번호
    - 비밀번호 확인
    - 연락처 (선택)
    - 이메일 (선택)
    - 주소 (선택)

 2. 로그인 화면을 구성하기 위해 필요한 항목을 생각해보자.
    - 아이디
    - 비밀번호

 3. 기타 화면의 필요한 내용들을 생각해보자.
    - 물품 정보 조회
        - 댓글
        - 댓글 답글
        - 구매제안
    - 물품 정보 등록
    - 구매 제안 목록 확인

```
```markdown
### 4일차 UI 링크

1. [메인페이지](http://localhost:1212/users/main)
2. [회원가입](http://localhost:1212/users/register)
3. [로그인](http://localhost:1212/users/issue)
4. [프로필 페이지](http://localhost:1212/users/profile) -> 실패.. 

```
#### 4. UI 구현하기 설명

```
src 폴더 밑에 templates 에서 login.html, main.html, profile.html, register.html 을 참고해주세요!
+ UserController를 참고해주세요!

Main page -> login , register 접근 가능하게 button으로 만들었습니다.

Login page -> register, main 접근 가능하게 button으로 만들었습니다.
아이디, 비밀번호를 입력하면 등록된 회원정보면 로그인 잘못된 정보면 로그인 실패!를 보여주게 만들었습니다.
-> alert("로그인 실패: " + error.message);

Register page -> login , main 접근 가능하게 button으로 만들었습니다.
사용자명 : 필수로 입력하게 해줬습니다. 이미 존재하면 가입 X 
비밀번호, 비밀번호 확인: 둘이 똑같이 입력하지 않으면 가입 X
전화번호, 주소, 이메일: 필수 정보는 아닙니다 

회원가입 실패하면 서버에서는 log로 확인 가능하지만 Client에게는 실패했다는 사실을 전달하지 못 하였습니다.
나중에 추가 예정!

지금까지 설명한 부분은 전부 token(jwt)인증을 하지 않아도 접근이 가능한 부분 입니다.
인증을 해야만 접근이 가능한 요청을 만들어 보려고 했는데 js 지식이 부족해서 실패하였습니다.
토큰 저장 방식은 localStorage를 사용하였습니다. 

```

## 읽어주셔서 감사합니다!!!


