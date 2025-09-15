package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.MatchConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 匹配配置 Mapper
 * 
 * @author yupi
 */
@Mapper
public interface MatchConfigMapper extends BaseMapper<MatchConfig> {

    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM match_config WHERE config_key = #{configKey} AND is_active = 1")
    String getConfigValue(@Param("configKey") String configKey);

    /**
     * 根据配置键获取配置值，如果不存在则返回默认值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    default String getConfigValueOrDefault(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整型配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    default Integer getIntConfigValue(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔型配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    default Boolean getBooleanConfigValue(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    /**
     * 获取双精度配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    default Double getDoubleConfigValue(String configKey, Double defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}