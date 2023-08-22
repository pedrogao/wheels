package github.io.pedrogao.tinyspring.beans.factory.config;

import github.io.pedrogao.tinyspring.beans.BeansException;
import github.io.pedrogao.tinyspring.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;

    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean,
                                                       String beanName) throws BeansException;

    Object applyBeanPostProcessorsAfterInitialization(Object existingBean,
                                                      String beanName) throws BeansException;
}