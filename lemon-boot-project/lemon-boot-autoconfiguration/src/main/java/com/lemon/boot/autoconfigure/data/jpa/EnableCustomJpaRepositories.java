package com.lemon.boot.autoconfigure.data.jpa;

import com.lemon.framework.db.jpa.repository.CustomSimpleJpaRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.lang.annotation.*;

@SuppressWarnings("unused")
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({JpaRepositoriesRegistrar.class})
@DependsOn("springContextUtils")
public @interface EnableCustomJpaRepositories {
    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    ComponentScan.Filter[] includeFilters() default {};

    ComponentScan.Filter[] excludeFilters() default {};

    String repositoryImplementationPostfix() default "Impl";

    String namedQueriesLocation() default "";

    QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    Class<?> repositoryFactoryBeanClass() default JpaRepositoryFactoryBean.class;

    Class<?> repositoryBaseClass() default CustomSimpleJpaRepository.class;

    String entityManagerFactoryRef() default "entityManagerFactory";

    String transactionManagerRef() default "transactionManager";

    boolean considerNestedRepositories() default false;

    boolean enableDefaultTransactions() default true;

    BootstrapMode bootstrapMode() default BootstrapMode.DEFAULT;

    char escapeCharacter() default '\\';
}
