package kr.co.amateurs.server.domain.post.model.entity;

import jakarta.persistence.*;
import kr.co.amateurs.server.domain.together.model.dto.MarketPostRequestDTO;
import kr.co.amateurs.server.common.model.entity.BaseEntity;
import kr.co.amateurs.server.domain.post.model.entity.enums.MarketStatus;
import lombok.*;

@Entity
@Table(name = "market_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MarketItem extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Post post;

    @Column(nullable = false)
    private Integer price;
    private String place;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketStatus status;

    public void update (MarketPostRequestDTO dto){
        this.status = dto.status();
        this.price = dto.price();
        this.place = dto.place();

    }

    public void updateStatus(MarketStatus status) {
        this.status = status;
    }
}
