package cn.tf.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * VerifyFieldExistUtil 提供基于 MyBatis-Plus 的字段值存在性校验工具，支持排除指定 ID 和逻辑删除字段的过滤。
 *
 * <p><strong>方法清单：</strong></p>
 * <ul>
 *   <li>{@link #contentExists(IService, SFunction, String, String)} - 校验字段值是否存在</li>
 *   <li>{@link #contentExists(IService, SFunction, SFunction, String, String)} - 支持逻辑删除字段的校验</li>
 *   <li>{@link #contentExists(IService, SFunction, SFunction, String, String, String)} - 支持排除 ID 的完整校验</li>
 * </ul>
 *
 * <p>用于例如用户名、编码、标识符等字段的唯一性检查，避免插入重复数据。</p>
 *
 * <p>注意：方法中使用 {@code apply} 拼接原生 SQL，调用方需确保字段名合法以防止注入。</p>
 *
 * @author tf
 */
@Component
public class VerifyFieldExistUtil {

    /**
     * 未删除的标志位，通常为逻辑删除字段的默认“未删除”值。
     * 可通过配置项 {@code logic-delete-flag.not-delete} 进行覆盖，默认值为 {@code 0}。
     */
    @Value("${logic-delete-flag.not-delete:0}")
    private String notDeleteFlag;

    /**
     * 校验某字段值是否存在（不包含逻辑删除判断、不排除 ID）。
     *
     * @param serviceInterface 实体的 service 接口
     * @param idGetter         主键字段 getter，用于 select 和比较
     * @param sqlFieldName     数据库中字段名（注意不是 Java 字段名）
     * @param fieldValue       要判断的字段值
     * @param <E>              实体类型
     * @return true 表示存在，false 表示不存在
     */
    public <E> boolean contentExists(final IService<E> serviceInterface,
                                     final SFunction<E, String> idGetter,
                                     final String sqlFieldName,
                                     final String fieldValue) {
        return contentExists(serviceInterface, idGetter, null, sqlFieldName, fieldValue);
    }

    /**
     * 校验字段值是否存在，支持逻辑删除字段判断（不排除 ID）。
     *
     * @param serviceInterface    实体的 service 接口
     * @param idGetter            主键字段 getter
     * @param logicDeleteFunction 逻辑删除字段 getter（为空则不判断逻辑删除）
     * @param sqlFieldName        数据库中字段名
     * @param fieldValue          要判断的字段值
     * @param <E>                 实体类型
     * @param <L>                 逻辑删除字段的类型
     * @return true 表示存在，false 表示不存在
     */
    public <E, L> boolean contentExists(final IService<E> serviceInterface,
                                        final SFunction<E, String> idGetter,
                                        final SFunction<E, L> logicDeleteFunction,
                                        final String sqlFieldName,
                                        final String fieldValue) {
        return contentExists(serviceInterface, idGetter, logicDeleteFunction, null, sqlFieldName, fieldValue);
    }

    /**
     * 校验字段值是否存在，支持逻辑删除判断，支持排除指定 ID（通常用于更新场景）。
     *
     * @param serviceInterface    实体的 service 接口
     * @param idGetter            主键字段 getter
     * @param logicDeleteFunction 逻辑删除字段 getter（可为空）
     * @param excludeId           需要排除的主键 ID（为空则不排除）
     * @param sqlFieldName        数据库中字段名
     * @param fieldValue          要判断的字段值
     * @param <E>                 实体类型
     * @param <L>                 逻辑删除字段的类型
     * @return true 表示存在（且非当前排除 ID），false 表示不存在或仅自身存在
     */
    public <E, L> boolean contentExists(final IService<E> serviceInterface,
                                        final SFunction<E, String> idGetter,
                                        final SFunction<E, L> logicDeleteFunction,
                                        final String excludeId,
                                        final String sqlFieldName,
                                        final String fieldValue) {
        if (SqlInjectionUtils.check(sqlFieldName)) {
            throw new RuntimeException("注意！存在注入内容：" + sqlFieldName);
        }

        Assert.notNull(fieldValue, "fieldValue 不能为空");

        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(idGetter);

        if (logicDeleteFunction != null) {
            queryWrapper.eq(logicDeleteFunction, notDeleteFlag);
        }

        // 防止因为字符集的问题出现大小写没校验住的情况
        queryWrapper.apply(" binary " + sqlFieldName + " = {0}", fieldValue.trim());

        List<E> list = serviceInterface.list(queryWrapper);

        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        String id = idGetter.apply(list.get(0));
        return !id.equals(excludeId);
    }
}
