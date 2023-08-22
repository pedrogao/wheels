package github.io.pedrogao.tinyspring.ioc;

import github.io.pedrogao.tinyspring.beans.factory.annotation.Autowired;

public class BaseService {

    @Autowired
    private BaseBaseService bbs;

    public BaseService(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public BaseService() {
    }

    public BaseBaseService getBbs() {
        return bbs;
    }

    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }
}
