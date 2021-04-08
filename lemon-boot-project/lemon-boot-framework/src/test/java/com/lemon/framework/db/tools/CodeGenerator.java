package com.lemon.framework.db.tools;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>名称：MyBatis代码生成器</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/17
 */
public class CodeGenerator {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (!StringUtils.hasLength(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 输出到子模块
        final String projectPath = System.getProperty("user.dir") + "/teymall-db";

        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("hai-zhang");
        gc.setOpen(false);
        gc.setIdType(IdType.AUTO);
        // 注意这块有个坑
        // mysql数据类型datetime会被mp生成器默认生成为LocalDateTime，从而产生异常
        // org.springframework.dao.InvalidDataAccessApiUsageException: Error attempting to get column 'xxx' from result set.
        // Cause: java.sql.SQLFeatureNotSupportedException
        // 解决方案：1.mybatis降到3.5.0版本；2.将生成器默认日期类型改为SQL_PACK
        gc.setDateType(DateType.SQL_PACK);
        // TODO: 记得关掉
//        gc.setFileOverride(true);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://39.105.232.141:3306/zy?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("teysoft2020!");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner("模块名"));
        pc.setParent("com.teysoft.mall.db");
        mpg.setPackageInfo(pc);

        // 自定义属性注入配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //数据库表字段映射到实体类的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //你自己的父类实体,没有就不用设置!
        //自定义继承entity类，添加这一个会在生成实体类的时候继承entity
//        strategy.setSuperEntityClass(com.teysoft.mall.db.entity.BaseEntity.class);
        //实体是否为lombok模型
        strategy.setEntityLombokModel(true);
        //生成@RestController控制器
        strategy.setRestControllerStyle(true);
        //是否继承controller
        //公共父类: 你自己的父类控制器,没有就不用设置!
//        strategy.setSuperControllerClass("");
        //写于父类中的公共字段
//        strategy.setSuperEntityColumns("id_");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        //驼峰转连字符串
        strategy.setControllerMappingHyphenStyle(true);
        //逻辑删除，如果全局指定了，这里可以不必指定
        strategy.setLogicDeleteFieldName("deleted_");
        //乐观锁
        strategy.setVersionFieldName("update_time_");
        //表前缀
        strategy.setTablePrefix(pc.getModuleName() + "_");
        //强制指定数据库中的字段名
        strategy.setEntityTableFieldAnnotationEnable(true);
        //自动填充列
        strategy.setTableFillList(Stream.of(
                new TableFill("add_time_", FieldFill.INSERT),
                new TableFill("update_time_", FieldFill.INSERT_UPDATE)
        ).collect(Collectors.toList()));
        //【实体】是否为构建者模型（默认 false）
//        strategy.setEntityBuilderModel(true);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
