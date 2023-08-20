package github.io.pedrogao.tinyspring.context;

import github.io.pedrogao.tinyspring.beans.*;
import github.io.pedrogao.tinyspring.beans.factory.BeanFactory;
import github.io.pedrogao.tinyspring.beans.factory.config.AutowireCapableBeanFactory;
import github.io.pedrogao.tinyspring.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import github.io.pedrogao.tinyspring.beans.factory.config.BeanFactoryPostProcessor;
import github.io.pedrogao.tinyspring.beans.factory.xml.XmlBeanDefinitionReader;
import github.io.pedrogao.tinyspring.core.ClassPathXmlResource;
import github.io.pedrogao.tinyspring.core.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {
    private final Logger log = LoggerFactory.getLogger(ClassPathXmlApplicationContext.class);

    private AutowireCapableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public ClassPathXmlApplicationContext(String filename) {
        this(filename, true);
    }

    public ClassPathXmlApplicationContext(String filename, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(filename);
        // beanFactory = new SimpleBeanFactory();
        beanFactory = new AutowireCapableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        if (isRefresh) {
            try {
                refresh();
            } catch (BeansException e) {
                log.error("Error refreshing application context", e);
                throw new RuntimeException(e);
            }
        }
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public void refresh() throws BeansException, IllegalStateException {
        // Register bean processors that intercept bean creation.
        registerBeanPostProcessors(this.beanFactory);
        // Initialize other special beans in specific context subclasses.
        onRefresh();
    }

    private void registerBeanPostProcessors(AutowireCapableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    private void onRefresh() {
        this.beanFactory.refresh();
    }

    public Object getBean(String beanName) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    public boolean containsBean(String beanName) {
        return beanFactory.containsBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        // TODO
        return false;
    }

    @Override
    public boolean isPrototype(String beanName) {
        // TODO
        return false;
    }

    @Override
    public Class<?> getType(String beanName) {
        // TODO
        return null;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        // TODO
    }
}
