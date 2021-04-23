package rxjava;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 名称：<p>
 * 描述：<p>
 * <p>
 * <p>
 * 参考TTL作者的文章
 *
 * @author hai-zhang
 * @see <a href="https://github.com/alibaba/transmittable-thread-local/issues/122">https://github.com/alibaba/transmittable-thread-local/issues/122</a>
 * @since 2020/5/19
 */
public class SessionCacheDemo {

    @BeforeEach
    public void beforeClass() {
        RxJavaPlugins.setScheduleHandler(TtlRunnable::get);
    }

    @Test
    public void getSomethingByCache() throws Exception {
        BizService bizService = new BizService();

        final Consumer<Object> printer = result ->
                System.out.printf("[%30s]: %s%n",
                        Thread.currentThread().getName(),
                        bizService.getCache());

        Flowable.just(bizService)
                .observeOn(Schedulers.io())
                .map(BizService::getItemByCache)
                .doOnNext(printer)
                .blockingSubscribe(printer);

        // 业务 在后续时刻 需要用到
        Object object = bizService.getItemByCache();
        printer.accept(object);
        bizService.clearCache();
    }

    /**
     * Mock Service
     */
    private static class BizService {
        private static final String ONLY_KEY = "ONLY_KEY";

        private final TransmittableThreadLocal<ConcurrentMap<String, Item>> cache_context =
                new TransmittableThreadLocal<ConcurrentMap<String, Item>>() {
                    @Override
                    protected ConcurrentMap<String, Item> initialValue() {
                        return new ConcurrentHashMap<>(); // init cache
                    }
                };

        public BizService() {
            // NOTE: AVOID cache object lazy init
            cache_context.get();
        }

        public Item getItem() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do nothing
            }
            return new Item(ThreadLocalRandom.current().nextInt(0, 10_000));
        }

        /**
         * 获取业务数据，一般使用spring cache做缓存，这里简单实现
         */
        public Item getItemByCache() {
            final ConcurrentMap<String, Item> cache = cache_context.get();
            return cache.computeIfAbsent(ONLY_KEY, key -> getItem());
        }

        public Item getCache() {
            return cache_context.get().get(ONLY_KEY);
        }

        public void clearCache() {
            cache_context.get().clear();
        }
    }

    /**
     * Mock Cache Data
     */
    private static class Item {
        private int id;

        public Item(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Item{id=" + id + '}';
        }
    }
}
