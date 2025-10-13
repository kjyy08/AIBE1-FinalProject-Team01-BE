package kr.co.amateurs.server.domain.alarm.model.dto;

import kr.co.amateurs.server.common.model.dto.PageInfo;
import kr.co.amateurs.server.domain.alarm.model.entity.Alarm;
import org.springframework.data.domain.Page;

import java.util.List;

public record AlarmPageDTO(
        List<AlarmDTO> content,
        long unReadCount,
        PageInfo pageInfo
) {
    public static AlarmPageDTO from(Page<Alarm> page, long unReadCount) {
        List<AlarmDTO> list = page.getContent().stream()
                .map(AlarmDTO::from)
                .toList();
        PageInfo info = PageInfo.from(page);
        return new AlarmPageDTO(list, unReadCount, info);
    }
}
