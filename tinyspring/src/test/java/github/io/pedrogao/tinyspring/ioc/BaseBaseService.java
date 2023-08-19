package github.io.pedrogao.tinyspring.ioc;

public class BaseBaseService {
    private AServiceImpl as;

    public BaseBaseService(AServiceImpl as) {
        this.as = as;
    }

    public BaseBaseService() {
    }

    public void setAs(AServiceImpl as) {
        this.as = as;
    }

    public AService getAs() {
        return as;
    }
}
