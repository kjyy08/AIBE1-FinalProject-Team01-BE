# 🧑‍💻Convention

## **팀 규칙 💡**

### 1. 회의 및 스크럼

- 오전 9시 수업 시작할 때 데일리 스크럼 (각자 작성 후 발표)
    - 어제 했던 일 리뷰 및 컨디션, 이슈 체크
    - 깃허브 프로젝트에 이슈 등록 후 프로젝트 칸반보드에 추가하기
- 매주 목요일 수업 시간 혹은 멘토링 전에 모여서 주간 회의
    - 진척도 체크, 해당 주에 공통으로 할 일 정하기
    - 회의록은 돌아가면서 작성 (지현숙 > 조경혜 > 이영빈 > 김주엽 > 김민호 > 권규태)

### 2. 멘토링 시간

- 멘토님과 조율하되, 목 or 금요일 저녁 진행
- 가능한 모두 참석하기
- 멘토링 일지는 돌아가면서 쓰기 (권규태 > 김민호 > 김주엽 > 이영빈 > 조경혜 > 지현숙)

### 3. 팀 문화

- 1시간 고민해보고 해결 안되는 건 바로 공유합시다
- 도움이 필요하면 요청해서 함께 해결합시다
- 서로 존중하되, 의견은 자유롭게 말할 수 있는 환경 조성합시다
- 정기 회의나 멘토링 등 일정 불참 시 미리 말합시다
- 공지사항 확인하면 ✅ 이모지 추가하기
- 이슈, 작업 관련 사항은 슬랙에 남기기
    - 논의가 필요한 사람이 `[이슈 사항]` 채팅

## **팀 컨벤션 🖊️**

## 1. 커밋 컨벤션

> 예시 : feat : 판매자 CRUD 기능 추가
>
- **Feat** : 새로운 기능 추가
- **Fix** : 버그 수정
- **Env** : 개발 환경 관련 설정
- **Style** : 코드 스타일 수정 (세미 콜론, 인덴트 등의 스타일적인 부분만)
- **Refactor** : 코드 리팩토링 (더 효율적인 코드로 변경 등)
- **Design** : CSS 등 디자인 추가/수정
- **Comment :** 주석 추가/수정
- **Docs :** 내부 문서 추가/수정
- **Test :** 테스트 추가/수정
- **Chore :** 빌드 관련 코드 수정
- **Rename :** 파일 및 폴더명 수정
- **Remove :** 파일 삭제

## 2. 코드 컨벤션

- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html)
- **들여쓰기**
    - 기본 설정을 사용하되, 마지막에 한 번에 정리하기
    - `if` 문의 경우 한 줄이더라도 중괄호 사용

        ```java
        if (name.equals("쏼라쏼라")) {
        	return name;
        }
        
        ```

- **DTO** : Response, Request(예시)

    ```java
    class PostCreateRequest {
    	...
    }
    
    ```

- **클래스명** : PascalCase
- **변수/함수명** : camelCase
- **경로명** : 연속 소문자(examplecase)
- **DB 칼럼 이름** : snake_case
- **주석** : 꼭 필요한 주석만 작성. 코드 들여쓰기에 맞춰서 작성
- **Builder** : 빌더 패턴, toBuilder 사용

    ```java
    public void modify(MemberModifyReqeustDto requestDto){
    
    //기존 객체 불러오기
        Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER));
    
        Member updateMember = member.toBuilder().
                    .age(requestDto.getAge())
                    .build();
    
        memberRepository.save(updateMember);
    }
    
    ```

- **API 명세** : RestDocs

## 3. 이슈 템플릿

- 브랜치는 이슈 단위로 파기
- 기능을 잘게 쪼개서 상세 기능 단위로 이슈 생성 > PR
- 제목

    ```
    feat : 기능 요약
    ex) feat : 로그인 기능 구현 / fix : 로그인 오류 해결
    
    ```

- 내용

    ```
    ## 🎯 이슈 요약
    > 이 이슈에서 다루고자 하는 내용을 한 줄로 설명해주세요.
    
    ## ✅ 작업 내용
    - [ ] TODO
    - [ ] TODO
    - [ ] TODO
    
    ## 😇 이때까지 끝낼게요!
    > 기능 개발 완료 예상 날짜를 작성해주세요. 신중하게 생각해요!
    
    ## 😵 참고할만한 자료 (선택)
    > 관련 문서, 링크, 스크린샷 등을 첨부해주세요.
    
    ## 🙇‍♀️ 이슈 확인했어요:)
    > 팀원에게 이슈 확인을 부탁해요! 확인한 팀원은 체크 표시를 해주세요.
    - [ ] 팀원명
    
    ```


## 4. PR 템플릿

- **PR 양식**

```
feat : 어쩌고 저쩌고 기재 #이슈번호

```

```
## #️⃣연관된 이슈
> ex) #이슈번호, #이슈번호

## 📝작업 내용
> 이번 PR에서 작업한 내용을 간략히 설명해주세요(이미지 첨부 가능)

## 이후 작업
> 이후에 작업할 내용을 추가해주세요
- [ ] 대댓글 기능 구현

## 💬리뷰 요구사항(선택) 및 기타 참고사항
> ex) 실행 시 yml 파일 필요합니다.

## ✅ 체크리스트
- [ ] 코드 점검 완료했습니다
- [ ] 문서/주석 최신화 완료했습니다

```

## 5. Git Flow

[사례로 이해하는 GitHub Flow](https://www.heropy.dev/p/6hdJi6)