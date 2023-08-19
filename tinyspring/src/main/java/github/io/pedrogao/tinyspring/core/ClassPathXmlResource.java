package github.io.pedrogao.tinyspring.core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.Iterator;

public class ClassPathXmlResource implements Resource {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassPathXmlResource.class);
    Document document;
    Element rootElement;
    Iterator<Element> elementIterator;

    public ClassPathXmlResource(String filename) {
        SAXReader saxReader = new SAXReader();
        URL xmlPath = getClass().getClassLoader().getResource(filename);

        try {
            document = saxReader.read(xmlPath);
        } catch (DocumentException e) {
            log.error("read xml error", e);
            throw new RuntimeException(e);
        }
        rootElement = document.getRootElement();
        elementIterator = rootElement.elementIterator();
    }

    @Override
    public boolean hasNext() {
        return elementIterator.hasNext();
    }

    @Override
    public Object next() {
        return elementIterator.next();
    }
}
