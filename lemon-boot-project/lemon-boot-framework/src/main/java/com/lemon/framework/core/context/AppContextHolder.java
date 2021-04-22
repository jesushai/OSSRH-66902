package com.lemon.framework.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.util.Assert;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
public class AppContextHolder {

    private static final TransmittableThreadLocal<AppContext> holder = new TransmittableThreadLocal<AppContext>() {
        /**
         * 定制化备份方法，这里是复制的引用
         * @param parentValue AppContext
         * @return AppContext
         */
        @Override
        public AppContext copy(AppContext parentValue) {
            return super.copy(parentValue);
        }

        @Override
        protected void beforeExecute() {
            super.beforeExecute();
        }

        @Override
        protected void afterExecute() {
            super.afterExecute();
        }

        /**
         * 重写初始化方法
         * @return AppContext
         */
        @Override
        protected AppContext initialValue() {
            return new AppContext();
        }
    };

    /**
     * 如果是Session级的缓存，避免内存泄漏请在Session结束的时候清理holder
     */
    public static void clearContext() {
        if (null != holder.get()) {
            holder.get().clear();
        }
        holder.remove();
    }

    public static AppContext getContext() {
        AppContext context = holder.get();
        if (context == null) {
            context = new AppContext();
            holder.set(context);
        }
        return context;
    }

    public static void setContext(AppContext context) {
        Assert.notNull(context, "Only non-AppContext instances are permitted");
        holder.set(context);
    }
}
