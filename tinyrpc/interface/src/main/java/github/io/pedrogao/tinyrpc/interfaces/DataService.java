package github.io.pedrogao.tinyrpc.interfaces;

import java.util.List;

public interface DataService {
    String sendData(String data);

    List<String> getList();
}
