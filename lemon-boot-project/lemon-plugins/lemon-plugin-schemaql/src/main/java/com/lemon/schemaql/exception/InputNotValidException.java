package com.lemon.schemaql.exception;

import com.lemon.framework.exception.MultiErrorException;

/**
 * <b>名称：输入的内容不符合验证</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/1
 */
public class InputNotValidException extends MultiErrorException {

    public InputNotValidException() {
        super();
    }
}
