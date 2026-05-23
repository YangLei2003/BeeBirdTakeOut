package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DishPageQueryDTO {
    private int page;
    private int pageSize;
    private Integer categoryId;
    private String name;
    private Integer status;
}
