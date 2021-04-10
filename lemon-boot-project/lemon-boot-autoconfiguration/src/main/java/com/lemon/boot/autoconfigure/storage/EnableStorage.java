package com.lemon.boot.autoconfigure.storage;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({StorageAutoConfiguration.class})
public @interface EnableStorage {
}
