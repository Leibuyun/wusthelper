package com.linghang.wusthelper.wustlib.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lby
 * @since 2022-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("book_borrow")
@ApiModel(value="BookBorrow对象", description="")
public class BookBorrow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "学生学号, 外键")
    private String studentNum;

    @ApiModelProperty(value = "书籍的bid, 外键")
    private String bid;

    @ApiModelProperty(value = "0: 当前借阅, 1:历史借阅")
    private Boolean flag;

    @ApiModelProperty(value = "借阅日期")
    private LocalDateTime borrowTime;

    @ApiModelProperty(value = "归还日期")
    private LocalDateTime backTime;


}
