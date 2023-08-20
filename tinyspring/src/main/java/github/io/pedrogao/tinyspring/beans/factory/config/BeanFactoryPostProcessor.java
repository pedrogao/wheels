package github.io.pedrogao.tinyspring.beans.factory.config;

import github.io.pedrogao.tinyspring.beans.BeansException;
import github.io.pedrogao.tinyspring.beans.factory.BeanFactory;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(BeanFactory beanFactory) throws BeansException;
}
