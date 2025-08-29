package org.spring.projectjs.JPA.App.Decor.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipRequest {
    private Long itemId;   // 클라가 slot을 보내지 않아도 OK
    private String slot;   // (선택) 보낼 경우 서버에서 교차검증
}
