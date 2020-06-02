package pers.lee.common.rpc.ci.convertor;

import pers.lee.common.lang.json.gson.GsonHandlerProvider;
import com.google.gson.GsonBuilder;

public class HibernateUtils {

    public static Object convertEntity(Object object) {
        GsonBuilder builder = GsonHandlerProvider.defaultGsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        builder.registerTypeAdapterFactory(HibernatePersistentCollection.FACTORY);

        return new GsonHandlerProvider(builder.create()).getSerializer().toJsonObject(object);
    }
}
