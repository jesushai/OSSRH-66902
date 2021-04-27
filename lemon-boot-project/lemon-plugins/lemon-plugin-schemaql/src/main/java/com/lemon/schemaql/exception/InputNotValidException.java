package com.lemon.schemaql.exception;

import com.lemon.framework.exception.MultiErrorException;
import com.lemon.framework.exception.support.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：输入的内容不符合验证<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/8/1
 */
public class InputNotValidException extends MultiErrorException {

    public InputNotValidException() {
        super();
        this.infos = new ArrayList<>();
    }

    public InputNotValidException addError(Message message) {
        super.addError(message);
        return this;
    }

    /**
     * 信息
     */
    private List<Message> infos;

    public List<Message> getInfos() {
        return infos;
    }

    public InputNotValidException addInfo(Message message) {
        infos.add(message);
        return this;
    }

    public void setInfos(List<Message> infos) {
        this.infos = infos;
    }
}
