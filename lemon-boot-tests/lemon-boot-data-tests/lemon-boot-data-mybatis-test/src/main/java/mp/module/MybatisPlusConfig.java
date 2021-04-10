package mp.module;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.User;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/18
 */
@EnableTransactionManagement
@Configuration
@MapperScan("mp.*.mapper*")
public class MybatisPlusConfig {

    @Resource
    private AuthenticationService authenticationService;

    private static final String excludeTenantTableNames = "sys_coding_category,";

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        //paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        //paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        // 自定义方言类、可以没有
        //paginationInterceptor.setDialect();

        // 创建SQL解析器集合
        List<ISqlParser> sqlParserList = new ArrayList<>();

        // region 租户解析器
        TenantSqlParser tenantSqlParser = new TenantSqlParser();

        tenantSqlParser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                // 从请求中获得租户
                User user = authenticationService.getPrincipal();
                return new LongValue(user.getTenant());
            }

            @Override
            public String getTenantIdColumn() {
                // 数据库表中租户ID的列名
                return "tenant_";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 是否需要需要过滤某一张表
                return excludeTenantTableNames.contains(tableName);
            }
        });

        sqlParserList.add(tenantSqlParser);
        // endregion

        // 自定义解析类、可以没有
        paginationInterceptor.setSqlParserList(sqlParserList);

        return paginationInterceptor;
    }

    /**
     * 乐观锁插件
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    /**
     * 自动填充审计信息
     */
    @Bean
    public MyFillMetaObjectHandler myFillMetaObjectHandler() {
        return new MyFillMetaObjectHandler();
    }
}
