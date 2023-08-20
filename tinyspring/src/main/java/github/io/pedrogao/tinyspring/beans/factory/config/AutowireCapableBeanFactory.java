package github.io.pedrogao.tinyspring.beans.factory.config;

import github.io.pedrogao.tinyspring.beans.BeansException;
import github.io.pedrogao.tinyspring.beans.factory.AbstractBeanFactory;
import github.io.pedrogao.tinyspring.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

public class AutowireCapableBeanFactory extends AbstractBeanFactory {
    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors = new ArrayList<>();
    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<AutowiredAnnotationBeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object obj, String beanName) throws BeansException {
        Object result = obj;
        for (AutowiredAnnotationBeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            beanProcessor.setBeanFactory(this);
            result = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return null;
            }
        }
        return result;
    }
    @Override
    public Object applyBeanPostProcessorAfterInitialization(Object obj, String beanName) throws BeansException {
        Object result = obj;
        for (AutowiredAnnotationBeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            beanProcessor.setBeanFactory(this);
            result = beanProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return null;
            }
        }
        return result;
    }
}
