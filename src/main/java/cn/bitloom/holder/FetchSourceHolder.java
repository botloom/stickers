package cn.bitloom.holder;

import cn.bitloom.enums.FetchSourceEnums;
import javafx.beans.property.BooleanProperty;

import java.util.List;

/**
 * The interface Fetch source config holder.
 *
 * @author bitloom
 */
public interface FetchSourceHolder {

    /**
     * Gets data source configs.
     *
     * @return the data source configs
     */
    List<FetchSourceConfig> getDataSourceConfigs();

    /**
     * The type Fetch source config.
     */
    record FetchSourceConfig(
            FetchSourceEnums dataSource,
            String displayName,
            String description,
            BooleanProperty selectedProperty
    ) {
    }
}
