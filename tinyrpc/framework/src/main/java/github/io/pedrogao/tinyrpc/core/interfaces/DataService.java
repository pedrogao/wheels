package github.io.pedrogao.tinyrpc.core.interfaces;

import java.util.List;

public interface DataService {
    String sendData(String data);

    List<String> getList();
}
