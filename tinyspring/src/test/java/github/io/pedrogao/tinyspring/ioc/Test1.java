package github.io.pedrogao.tinyspring.ioc;

import github.io.pedrogao.tinyspring.beans.BeansException;
import github.io.pedrogao.tinyspring.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = (AService) context.getBean("aservice");
        aService.sayHello();
    }
}
