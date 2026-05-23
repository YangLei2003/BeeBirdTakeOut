package com.sky.vo;

import com.sky.entity.DishFlavor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishVO implements Serializable {
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;//BigDecimal计算精度高
    private String image;
    private String description;
    private Integer status;//Integer可以为null，int必须要传值
    private LocalDateTime updateTime;
    private String categoryName;
    //
    private List<DishFlavor> flavors = new ArrayList<>();
}
