package com.lemon.framework.protocal;

import com.lemon.framework.exception.LoggableRuntimeException;
import com.lemon.framework.exception.MultiErrorException;
import com.lemon.framework.exception.support.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 名称：Rest请求返回的统一结果对象<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/5/16
 */
@Data
@ToString
public class Result implements Serializable {

    private static final long serialVersionUID = 815359742381406483L;

    private final String code;

    private String msg;

    private final Collection<Message> errors = new ArrayList<>();

    private final Collection<Message> infos = new ArrayList<>();

    private Object data;

    /**
     * 分页相关信息
     */
    private PageInfo page;

    @Data
    @ToString
    @AllArgsConstructor
    public static class PageInfo {

        /**
         * 页总数
         */
        private int totalPages;

//        /**
//         * 每页记录数
//         */
//        private int pageSize;

        /**
         * 总记录数
         */
        private long totalElements;

        /**
         * 当前页号
         */
        private int number;

        /**
         * 当前页记录数
         */
        private int numberOfElements;
    }

    private Result(String code) {
        this.code = code;
        this.msg = "";
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(LoggableRuntimeException e) {
        this.code = e.getCode();
        this.msg = e.getMessage();
        if (e instanceof MultiErrorException) {
            this.errors.addAll(((MultiErrorException) e).getErrors());
        } else {
            this.addError(e.getErrorMessage());
        }
    }

    /**
     * @return 返回空的成功状态
     */
    public static Result ok() {
        return new Result("200");
    }

    public static Result error(LoggableRuntimeException e) {
        return new Result(e);
    }

    public Result data(Object data) {
        if (data != null && data.getClass().getName().equals("org.springframework.data.domain.Page")) {
            Page<?> page = (Page<?>) data;
            this.data = page.getContent();
            this.page = new PageInfo(
                    page.getTotalPages(),       // 总页数
//                    page.getSize(),             // 每页记录数
                    page.getTotalElements(),    // 总记录数
                    page.getNumber(),           // 当前页号，0开始
                    page.getNumberOfElements()  // 当前页的记录数
            );
        } else if (data != null && data.getClass().getName().equals("com.baomidou.mybatisplus.extension.plugins.pagination.Page")) {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> page = (com.baomidou.mybatisplus.extension.plugins.pagination.Page<?>) data;
            this.data = page.getRecords();
            this.page = new PageInfo(
                    (int) page.getPages(),      // 总页数
//                    (int) page.getSize(),       // 每页记录数（无意义）
                    page.getTotal(),            // 总记录数
                    (int) page.getCurrent(),    // 当前页号，1开始
                    (int) page.getSize()        // 当前页的记录数
            );
        } else {
            this.data = data;
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Result addError(Message error) {
        this.errors.add(error);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Result addInfo(Message info) {
        this.infos.add(info);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Result addInfos(List<Message> infos) {
        this.infos.addAll(infos);
        return this;
    }

}
