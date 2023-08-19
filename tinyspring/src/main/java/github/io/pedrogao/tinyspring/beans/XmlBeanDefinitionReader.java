package github.io.pedrogao.tinyspring.beans;

import github.io.pedrogao.tinyspring.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class XmlBeanDefinitionReader {
    private final SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Object next = resource.next();
            if (next instanceof Element element) {
                String beanId = element.attributeValue("id");
                String beanClass = element.attributeValue("class");
                BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClass);

                List<Element> constructorElements = element.elements("constructor-arg");
                ArgumentValues argumentValues = new ArgumentValues();
                for (Element e : constructorElements) {
                    String pType = e.attributeValue("type");
                    String pName = e.attributeValue("name");
                    String pValue = e.attributeValue("value");
                    argumentValues.addArgumentValue(new ArgumentValue(pType, pName, pValue));
                }
                beanDefinition.setConstructorArgumentValues(argumentValues);

                List<Element> propertyElements = element.elements("property");
                PropertyValues propertyValues = new PropertyValues();
                List<String> refs = new ArrayList<>();
                for (Element e : propertyElements) {
                    String pType = e.attributeValue("type");
                    String pName = e.attributeValue("name");
                    String pValue = e.attributeValue("value");
                    String pRef = e.attributeValue("ref");
                    String pV = "";
                    boolean isRef = false;
                    if (pValue != null && !pValue.isEmpty()) {
                        pV = pValue;
                        isRef = false;
                    } else if (pRef != null && !pRef.isEmpty()) {
                        pV = pRef;
                        isRef = true;
                        refs.add(pRef);
                    }
                    propertyValues.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
                }
                beanDefinition.setPropertyValues(propertyValues);
                String[] refArray = refs.toArray(new String[0]);
                beanDefinition.setDependsOn(refArray);

                simpleBeanFactory.registerBeanDefinition(beanId, beanDefinition);
            }
        }
    }
}
