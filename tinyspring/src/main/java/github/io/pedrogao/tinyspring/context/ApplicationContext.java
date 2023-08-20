package github.io.pedrogao.tinyspring.context;

import github.io.pedrogao.tinyspring.beans.BeansException;
import github.io.pedrogao.tinyspring.beans.factory.config.ConfigurableBeanFactory;
import github.io.pedrogao.tinyspring.beans.factory.ListableBeanFactory;
import github.io.pedrogao.tinyspring.beans.factory.config.BeanFactoryPostProcessor;
import github.io.pedrogao.tinyspring.beans.factory.config.ConfigurableListableBeanFactory;
import github.io.pedrogao.tinyspring.core.env.Environment;
import github.io.pedrogao.tinyspring.core.env.EnvironmentCapable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory,
        ConfigurableBeanFactory, ApplicationEventPublisher {
    String getApplicationName();

    long getStartupDate();

    ConfigurableListableBeanFactory getBeanFactory() throws
            IllegalStateException;

    void setEnvironment(Environment environment);

    Environment getEnvironment();

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void refresh() throws BeansException, IllegalStateException;

    void close();

    boolean isActive();
}