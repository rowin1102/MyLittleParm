package org.spring.projectjs.JPA.App.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFurnitureId implements Serializable {
    private Long userIdx;
    private Long furnId;
}