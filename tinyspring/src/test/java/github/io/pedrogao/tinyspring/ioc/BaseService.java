package github.io.pedrogao.tinyspring.ioc;

public class BaseService {
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
