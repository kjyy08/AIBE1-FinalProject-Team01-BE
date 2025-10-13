package kr.co.amateurs.server.common.model.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostPaginationParam extends PaginationParam {
    @Builder.Default
    private String keyword = "";
}
