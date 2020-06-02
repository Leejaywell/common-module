package pers.lee.common.rpc.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jay
 * @date
 */
public class RpcConfig {
    private Set<String> domains;
    private Map<RpcKey, RpcInvoker> rpcMap;

    public RpcConfig() {
        domains = new HashSet<>();
        rpcMap = new HashMap<>();
    }

    public RpcConfig(Set<String> domains, Map<RpcKey, RpcInvoker> rpcMap) {
        this.domains = domains;
        this.rpcMap = rpcMap;
    }

    public Map<RpcKey, RpcInvoker> getRpcMap() {
        return rpcMap;
    }

    public void setRpcMap(Map<RpcKey, RpcInvoker> rpcMap) {
        this.rpcMap = rpcMap;
    }

    public Set<String> getDomains() {
        return domains;
    }

    public void setDomains(Set<String> domains) {
        this.domains = domains;
    }

    public RpcInvoker getRPCInvoker(RpcKey rpcKey) {
        return rpcMap.get(rpcKey);
    }

    public RpcInvoker addRPCInvoker(RpcKey rpcKey, RpcInvoker rpcInvoker) {
        return rpcMap.put(rpcKey, rpcInvoker);
    }

    public void addDomain(String domain) {
        domains.add(domain);
    }

    public void extendRPC(Set<String> domains, Map<RpcKey, RpcInvoker> rpcMap) {
        this.domains.addAll(domains);
        this.rpcMap.putAll(rpcMap);
    }

    public static class RpcKey {
        private String path;
        private String method;

        public RpcKey(String path, String method) {
            this.path = path;
            this.method = method;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof RpcKey))
                return false;

            RpcKey rpcKey = (RpcKey) o;

            if (method != null ? !method.equals(rpcKey.method) : rpcKey.method != null)
                return false;
            if (path != null ? !path.equals(rpcKey.path) : rpcKey.path != null)
                return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (path != null ? path.hashCode() : 0);
            result = 31 * result + (method != null ? method.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "RpcKey{" + "path='" + path + '\'' + ", method='" + method + '\'' + '}';
        }
    }
}