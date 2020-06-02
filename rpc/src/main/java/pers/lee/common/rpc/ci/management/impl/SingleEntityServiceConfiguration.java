package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.rpc.ci.management.SingleEntityService;

import java.util.Set;

/**
 * @author YangYang
 * @version 0.1, 2008-12-3 10:36:42
 */
public class SingleEntityServiceConfiguration {
    private Set<SingleEntityService> singleEntityServices;

    public Set<SingleEntityService> getSingleEntityServices() {
        return singleEntityServices;
    }

    public void setSingleEntityServices(Set<SingleEntityService> singleEntityServices) {
        this.singleEntityServices = singleEntityServices;
    }
}
