package pers.lee.common.rpc.ci.query;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public class GroupDescriptor extends ResultDescriptor {

	private Set<Aggregator> aggregators = new HashSet<>();

	public Set<Aggregator> getAggregators() {
		return aggregators;
	}

	public void setAggregators(Set<Aggregator> aggregators) {
		this.aggregators = aggregators;
	}

	public void addAggregator(Aggregator aggregator) {
		if (this.aggregators == null) {
			this.aggregators = new HashSet<Aggregator>();
		}

		this.aggregators.add(aggregator);
	}
    
    public Set<String> getGroupProperties() {
        return this.getReturnProperties();
    }

    public void setGroupProperties(Set<String> groupProperties) {
        this.setReturnProperties(groupProperties);
    }
}
