package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;

import java.util.Map;

/**
 * RPCConfiguration
 *
 * @author Drizzt Yang
 */
public class RemoteConfiguration extends ReloadableConfiguration implements Configuration {
	
	private static final String PROP_TIMESTAMP = "@timestamp";
	
    private RemoteSource remoteSource;

    public RemoteConfiguration(String name,  RemoteSource remoteSource, boolean reloadActivated) {
        super(name, reloadActivated);
        this.remoteSource = remoteSource;
    }

    @Override
    protected long getCurrentTimestamp() {
        return remoteSource.getCurrentTimestamp();
    }

	@Override
	protected Map<String, String> reload() {
		Map<String, String> props = remoteSource.getAll();
		if (props.containsKey(PROP_TIMESTAMP)) {
			this.timestamp = Long.valueOf(props.get(PROP_TIMESTAMP));
			props.remove(PROP_TIMESTAMP);
		} else {
			this.timestamp = this.getCurrentTimestamp();
		}
		return props;
	}

    @Override
    public boolean available() {
        return remoteSource.available();
    }

    @Override
    public void setProperty(String key, String value) {
        remoteSource.setProperty(key, value);
        super.setProperty(key, value);
    }

    @Override
    public void clearProperty(String key) {
        remoteSource.setProperty(key, null);
        super.clearProperty(key);
    }
}
