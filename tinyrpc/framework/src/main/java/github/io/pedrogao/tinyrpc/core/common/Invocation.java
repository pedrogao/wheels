package github.io.pedrogao.tinyrpc.core.common;

import java.util.Arrays;

public class Invocation {
    private String targetMethod;
    private String targetServiceName;
    private Object[] args;
    private String uuid;
    private Object response;

    public Invocation() {
    }

    public Invocation(String targetMethod, String targetServiceName, Object[] args, String uuid) {
        this.targetMethod = targetMethod;
        this.targetServiceName = targetServiceName;
        this.args = args;
        this.uuid = uuid;
    }

    public Invocation(String targetMethod, String targetServiceName, Object[] args, String uuid, Object response) {
        this.targetMethod = targetMethod;
        this.targetServiceName = targetServiceName;
        this.args = args;
        this.uuid = uuid;
        this.response = response;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getTargetServiceName() {
        return targetServiceName;
    }

    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "RpcInvocation{" +
                "targetMethod='" + targetMethod + '\'' +
                ", targetServiceName='" + targetServiceName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", uuid='" + uuid + '\'' +
                ", response=" + response +
                '}';
    }
}
