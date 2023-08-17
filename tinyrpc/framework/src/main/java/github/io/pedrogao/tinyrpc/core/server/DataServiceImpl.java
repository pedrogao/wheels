package github.io.pedrogao.tinyrpc.core.server;

import github.io.pedrogao.tinyrpc.interfaces.DataService;

import java.util.Arrays;
import java.util.List;

public class DataServiceImpl implements DataService {
    @Override
    public String sendData(String data) {
        return "success";
    }

    @Override
    public List<String> getList() {
        return Arrays.asList("a", "b", "c");
    }
}
