package pers.lee.common.lang.json;

public interface JsonHandlerProvider {

    /**
     * @return
     */
    JsonDeserializer getDeserializer();

    /**
     * @return
     */
    JsonSerializer getSerializer();

}
