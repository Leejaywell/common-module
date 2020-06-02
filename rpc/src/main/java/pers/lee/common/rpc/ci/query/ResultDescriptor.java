package pers.lee.common.rpc.ci.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author YangYang
 * @version 0.1, 2008-5-27 21:32:48
 */
public class ResultDescriptor {
	
    private Set<String> returnProperties;
    private Integer size;
    private List<OrderRule> orders;
    private Integer startIndex = 0;

    public Set<String> getReturnProperties() {
        return returnProperties;
    }

    public void setReturnProperties(Set<String> returnProperties) {
        this.returnProperties = returnProperties;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<OrderRule> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderRule> orders) {
        this.orders = orders;
    }

    public void addOrder(OrderRule property) {
        if(this.orders == null) {
            this.orders = new ArrayList<OrderRule>();
        }
        this.orders.add(property);
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }
}
