package cn.tf.utils;

import java.util.*;

/**
 * CollectionsUtils 提供了对 Java 集合的空判断与快速初始化方法，增强集合操作的便利性。
 *
 * <p><strong>方法清单：</strong></p>
 * <ul>
 *   <li>{@link #isEmpty(Collection)} - 判断集合是否为空</li>
 *   <li>{@link #isNotEmpty(Collection)} - 判断集合是否非空</li>
 *   <li>{@link #emptyArrayList()} - 返回一个空的 ArrayList 实例</li>
 *   <li>{@link #emptyHashMap()} - 返回一个空的 HashMap 实例</li>
 *   <li>{@link #emptyHashSet()} - 返回一个空的 HashSet 实例</li>
 * </ul>
 *
 * <p>所有方法均为静态方法，适合在工具类中直接调用。</p>
 *
 * @author tf
 */
@SuppressWarnings("rawtypes")
public class CollectionsUtils {

    /**
     * 判断给定的集合是否为空。
     *
     * @param collection 待判断的集合
     * @return 如果集合为 null 或为空，返回 true；否则返回 false
     */
    public static boolean isEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断给定的集合是否非空。
     *
     * @param collection 待判断的集合
     * @return 如果集合非 null 且不为空，返回 true；否则返回 false
     */
    public static boolean isNotEmpty(final Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 返回一个空的 ArrayList 实例。
     *
     * @return 空 ArrayList，初始容量为 0
     */
    public static ArrayList emptyArrayList() {
        return new ArrayList(0);
    }

    /**
     * 返回一个空的 HashMap 实例。
     *
     * @return 空 HashMap，初始容量为 0
     */
    public static Map emptyHashMap() {
        return new HashMap<>(0);
    }

    /**
     * 返回一个空的 HashSet 实例。
     *
     * @return 空 HashSet，初始容量为 0
     */
    public static Set emptyHashSet() {
        return new HashSet(0);
    }
}
