package com.lemon.boot.autoconfigure.data.jpa;

import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;

class JpaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
    JpaRepositoriesRegistrar() {
    }

    @NonNull
    protected Class<? extends Annotation> getAnnotation() {
        return EnableCustomJpaRepositories.class;
    }

    @NonNull
    protected RepositoryConfigurationExtension getExtension() {
        return new JpaRepositoryConfigExtension();
    }
}
