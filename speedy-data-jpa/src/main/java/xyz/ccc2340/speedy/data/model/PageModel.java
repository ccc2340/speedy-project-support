package xyz.ccc2340.speedy.data.model;

import lombok.Data;

import java.util.List;

/**
 * @Description 分页数据模型
 * @Author chenguangxue
 * @CreateDate 2018/06/08 17:48
 */
@Data
public class PageModel {

    private List<Object> data;
    private int index;
    private int cpp;
    private int totalCount;
    private int first;
    private int last;

    public PageModel() {
    }

    public PageModel(List<Object> data, int index, int cpp) {
        this.data = data;
        this.index = index;
        this.cpp = cpp;
    }
}
