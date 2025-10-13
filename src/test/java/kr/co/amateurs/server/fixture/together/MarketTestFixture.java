package kr.co.amateurs.server.fixture.together;

import kr.co.amateurs.server.domain.together.model.dto.MarketPostRequestDTO;
import kr.co.amateurs.server.domain.post.model.entity.MarketItem;
import kr.co.amateurs.server.domain.post.model.entity.Post;
import kr.co.amateurs.server.domain.post.model.entity.enums.BoardType;
import kr.co.amateurs.server.domain.post.model.entity.enums.MarketStatus;
import kr.co.amateurs.server.domain.user.model.entity.User;

import static kr.co.amateurs.server.domain.post.model.entity.Post.convertTagToList;


public class MarketTestFixture {
    public static MarketPostRequestDTO createMarketPostRequestDTO() {
        return new MarketPostRequestDTO(
                "Java 책",
                "Java 책 중고로 팝니다.",
                convertTagToList("책"),
                MarketStatus.SELLING,
                10000,
                "서울"
        );
    }

    public static Post createJavaPost(User user) {
        return Post.builder()
                .user(user)
                .boardType(BoardType.MARKET)
                .title("Java 책")
                .content("Java 책 중고로 팝니다.")
                .tags("책,자바")
                .build();
    }

    public static Post createPythonPost(User user) {
        return Post.builder()
                .user(user)
                .boardType(BoardType.MARKET)
                .title("Python 책")
                .content("Python 책 중고로 팝니다.")
                .tags("책,파이썬")
                .build();
    }

    public static MarketItem createJavaMarketItem(Post post) {
        return MarketItem.builder()
                .post(post)
                .status(MarketStatus.SELLING)
                .price(10000)
                .place("서울")
                .build();
    }

    public static MarketItem createPythonMarketItem(Post post) {
        return MarketItem.builder()
                .post(post)
                .status(MarketStatus.SELLING)
                .price(15000)
                .place("서울")
                .build();
    }
}