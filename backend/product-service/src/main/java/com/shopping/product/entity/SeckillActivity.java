package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("seckill_activities")
public class SeckillActivity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}