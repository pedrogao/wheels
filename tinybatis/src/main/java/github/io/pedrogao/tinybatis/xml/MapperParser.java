package github.io.pedrogao.tinybatis.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapperParser {
    public MapperNode parse(InputStream is) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(is);

        Element mapperElement = document.getRootElement();
        if (!mapperElement.getName().equals("mapper")) {
            throw new RuntimeException("root should be <mapper>");
        }

        String namespace = mapperElement.attribute("namespace").getValue();
        List<Element> selectElements = mapperElement.elements("select");
        List<SelectNode> selectNodes = new ArrayList<>(selectElements.size());
        for (Element selectElement : selectElements) {
            String id = selectElement.attribute("id").getValue();
            String resultType = selectElement.attribute("resultType").getValue();
            String sql = selectElement.getTextTrim(); // SQL
            SelectNode selectNode = new SelectNode(id, resultType, sql);
            selectNodes.add(selectNode);
        }
        return new MapperNode(namespace, selectNodes);
    }
}
