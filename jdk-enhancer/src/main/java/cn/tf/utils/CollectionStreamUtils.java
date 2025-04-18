package cn.tf.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CollectionStreamUtils 提供对集合数据通过 Java Stream API 的常用操作封装，
 * 简化集合转换为 List、Set、Map 等结构的处理过程，并支持可选的过滤条件。
 * <p><strong>方法清单：</strong></p>
 * <ul>
 *   <li>{@link #toDistinctList(Collection, Function)} - 提取字段并返回去重 List</li>
 *   <li>{@link #toDistinctList(Collection, Function, Predicate)} - 支持过滤的去重 List</li>
 *   <li>{@link #toList(Collection, Function)} - 提取字段并返回 List</li>
 *   <li>{@link #toList(Collection, Function, Predicate)} - 支持过滤的 List</li>
 *   <li>{@link #toSet(Collection, Function)} - 提取字段并返回 Set</li>
 *   <li>{@link #toSet(Collection, Function, Predicate)} - 支持过滤的 Set</li>
 *   <li>{@link #toMap(Collection, Function)} - 使用指定字段为 key，元素为 value 构造 Map</li>
 *   <li>{@link #toMap(Collection, Function, Predicate)} - 支持过滤的 key-value 映射</li>
 *   <li>{@link #toMap(Collection, Function, Function)} - 指定 key 和 value 的 Map</li>
 *   <li>{@link #toMap(Collection, Function, Function, Predicate)} - 支持过滤的 key-value Map</li>
 *   <li>{@link #groupingBy(Collection, Function)} - 按 key 分组</li>
 *   <li>{@link #groupingBy(Collection, Function, Predicate)} - 支持过滤的分组</li>
 * </ul>
 *
 * <p>所有方法均为静态方法，建议通过类名直接调用。</p>
 * @author tf
 */
public class CollectionStreamUtils {

    /**
     * 将集合转换为去重后的 List。
     *
     * @param dataList    原始集合
     * @param fieldGetter 用于提取字段的函数
     * @param <D>         原始集合元素类型
     * @param <F>         字段类型
     * @return 去重后的 List，若原集合为空返回空 List
     */
    public static <D, F> List<F> toDistinctList(final Collection<D> dataList, final Function<D, F> fieldGetter) {
        return toDistinctList(dataList, fieldGetter, null);
    }

    /**
     * 将集合转换为去重后的 List，并支持过滤。
     *
     * @param dataList    原始集合
     * @param fieldGetter 用于提取字段的函数
     * @param filter      可选过滤条件，为 null 则不过滤
     * @param <D>         原始集合元素类型
     * @param <F>         字段类型
     * @return 去重后的 List，若原集合为空返回空 List
     */
    public static <D, F> List<F> toDistinctList(final Collection<D> dataList, final Function<D, F> fieldGetter, Predicate<? super D> filter) {
        if (CollectionsUtils.isEmpty(dataList)) {
            return CollectionsUtils.emptyArrayList();
        }
        return getStream(dataList, filter).map(fieldGetter).distinct().collect(Collectors.toList());
    }

    /**
     * 将集合映射为 List。
     *
     * @param dataList    原始集合
     * @param fieldGetter 字段提取函数
     * @param <D>         原始元素类型
     * @param <F>         字段类型
     * @return 映射后的 List，若集合为空返回空 List
     */
    public static <D, F> List<F> toList(final Collection<D> dataList, final Function<D, F> fieldGetter) {
        return toList(dataList, fieldGetter, null);
    }

    /**
     * 将集合映射为 List，支持过滤。
     *
     * @param dataList    原始集合
     * @param fieldGetter 字段提取函数
     * @param filter      可选过滤条件
     * @param <D>         原始元素类型
     * @param <F>         字段类型
     * @return 映射后的 List，若集合为空返回空 List
     */
    public static <D, F> List<F> toList(final Collection<D> dataList, final Function<D, F> fieldGetter, Predicate<? super D> filter) {
        if (CollectionsUtils.isEmpty(dataList)) {
            return CollectionsUtils.emptyArrayList();
        }
        return getStream(dataList, filter).map(fieldGetter).collect(Collectors.toList());
    }

    /**
     * 将集合映射为 Set。
     *
     * @param dataList    原始集合
     * @param fieldGetter 字段提取函数
     * @param <D>         原始元素类型
     * @param <F>         字段类型
     * @return 映射后的 Set，若集合为空返回空 Set
     */
    public static <D, F> Set<F> toSet(final Collection<D> dataList, final Function<D, F> fieldGetter) {
        return toSet(dataList, fieldGetter, null);
    }

    /**
     * 将集合映射为 Set，支持过滤。
     *
     * @param dataList    原始集合
     * @param fieldGetter 字段提取函数
     * @param filter      可选过滤条件
     * @param <D>         原始元素类型
     * @param <F>         字段类型
     * @return 映射后的 Set，若集合为空返回空 Set
     */
    public static <D, F> Set<F> toSet(final Collection<D> dataList, final Function<D, F> fieldGetter, Predicate<? super D> filter) {
        if (CollectionsUtils.isEmpty(dataList)) {
            return CollectionsUtils.emptyHashSet();
        }
        return getStream(dataList, filter).map(fieldGetter).collect(Collectors.toSet());
    }

    /**
     * 将集合转换为 Map，key 为字符串，value 为对象本身。
     *
     * @param dataList 原始集合
     * @param keyGetter 键提取函数
     * @param <D> 原始元素类型
     * @return 映射后的 Map，若集合为空返回空 Map
     */
    public static <D> Map<String, D> toMap(final Collection<D> dataList, final Function<D, String> keyGetter) {
        return toMap(dataList, keyGetter, (Predicate<? super D>) null);
    }

    /**
     * 将集合转换为 Map，支持过滤，key 为字符串，value 为对象本身。
     *
     * @param dataList 原始集合
     * @param keyGetter 键提取函数
     * @param filter 可选过滤条件
     * @param <D> 原始元素类型
     * @return 映射后的 Map，若集合为空返回空 Map
     */
    public static <D> Map<String, D> toMap(final Collection<D> dataList, final Function<D, String> keyGetter, Predicate<? super D> filter) {
        if (CollectionsUtils.isEmpty(dataList)) {
            return CollectionsUtils.emptyHashMap();
        }
        return getStream(dataList, filter).collect(Collectors.toMap(keyGetter, Function.identity(), (a, b) -> b));
    }

    /**
     * 将集合转换为 Map，key 和 value 均由函数指定。
     *
     * @param dataList 原始集合
     * @param keyGetter 键提取函数
     * @param valueGetter 值提取函数
     * @param <D> 原始元素类型
     * @param <V> 映射后值类型
     * @return 映射后的 Map，若集合为空返回空 Map
     */
    public static <D, V> Map<String, V> toMap(final Collection<D> dataList, final Function<D, String> keyGetter, final Function<D, V> valueGetter) {
        return toMap(dataList, keyGetter, valueGetter, null);
    }

    /**
     * 将集合转换为 Map，支持过滤，key 和 value 均由函数指定。
     *
     * @param dataList 原始集合
     * @param keyGetter 键提取函数
     * @param valueGetter 值提取函数
     * @param filter 可选过滤条件
     * @param <D> 原始元素类型
     * @param <V> 映射后值类型
     * @return 映射后的 Map，若集合为空返回空 Map
     */
    public static <D, V> Map<String, V> toMap(final Collection<D> dataList, final Function<D, String> keyGetter, final Function<D, V> valueGetter, Predicate<? super D> filter) {
        if (CollectionsUtils.isEmpty(dataList)) {
            return CollectionsUtils.emptyHashMap();
        }
        return getStream(dataList, filter).collect(Collectors.toMap(keyGetter, valueGetter, (a, b) -> b));
    }

    /**
     * 将集合按指定 key 分组为 Map。
     *
     * @param dataList 原始集合
     * @param keyGetter 键提取函数
     * @param <D> 原始元素类型
     * @return 分组后的 Map，若集合为空返回空 Map
     */
    public static <D> Map<String, List<D>> groupingBy(final Collection<D> dataList, final Function<D, String> keyGetter) {
        return groupingBy(dataList, keyGetter, null);
    }

    /**
     * 将集合按指定 key 分组为 Map，支持过滤。
     *
     * @param dataList 原始集合
     * @param keyGetter 键提取函数
     * @param filter 可选过滤条件
     * @param <D> 原始元素类型
     * @return 分组后的 Map，若集合为空返回空 Map
     */
    public static <D> Map<String, List<D>> groupingBy(final Collection<D> dataList, final Function<D, String> keyGetter, Predicate<? super D> filter) {
        if (CollectionsUtils.isEmpty(dataList)) {
            return CollectionsUtils.emptyHashMap();
        }
        return getStream(dataList, filter).collect(Collectors.groupingBy(keyGetter));
    }

    /**
     * 获取集合的 Stream 流，支持条件过滤。
     *
     * @param dataList 原始集合
     * @param filter 可选过滤条件
     * @param <D> 元素类型
     * @return Stream 流
     */
    private static <D> Stream<D> getStream(final Collection<D> dataList, Predicate<? super D> filter) {
        return filter == null ? dataList.stream() : dataList.stream().filter(filter);
    }
}
