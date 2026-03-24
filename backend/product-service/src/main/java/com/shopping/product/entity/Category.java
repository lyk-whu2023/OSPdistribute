package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("categories")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    private Integer level;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}