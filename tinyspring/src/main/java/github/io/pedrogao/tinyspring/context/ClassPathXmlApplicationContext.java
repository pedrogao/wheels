package github.io.pedrogao.tinyspring.context;

import github.io.pedrogao.tinyspring.beans.*;
import github.io.pedrogao.tinyspring.core.ClassPathXmlResource;
import github.io.pedrogao.tinyspring.core.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {
    private final Logger log = LoggerFactory.getLogger(ClassPathXmlApplicationContext.class);

    private final SimpleBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String filename) {
        Resource resource = new ClassPathXmlResource(filename);
        beanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
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
