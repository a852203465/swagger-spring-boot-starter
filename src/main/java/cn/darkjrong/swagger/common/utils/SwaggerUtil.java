package cn.darkjrong.swagger.common.utils;

import cn.darkjrong.swagger.common.enums.ScanMatchingRuleEnum;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import springfox.documentation.RequestHandler;

import java.util.List;
import java.util.function.Predicate;

/**
 * Swagger工具类
 *
 * @author 贾荣
 * @date 2021/05/13 17:22
 */
public class SwaggerUtil {

	/**
	 * 获取包集合
	 *
	 * @param basePackages         多个包名集合
	 * @param scanMatchingRuleEnum 扫描匹配的规则枚举
	 * @return {@link Predicate}<{@link RequestHandler}>
	 */
	public static Predicate<RequestHandler> basePackages(ScanMatchingRuleEnum scanMatchingRuleEnum, final List<String> basePackages) {
		return input -> declaringClass(input).transform(handlerPackage(scanMatchingRuleEnum, basePackages)).or(true);
	}

	/**
	 * 校验基础包
	 *
	 * @param basePackages         基础包路径
	 * @param scanMatchingRuleEnum 扫描匹配的规则枚举
	 * @return {@link Function}<{@link Class}<{@link ?}>, {@link Boolean}>
	 */
	private static Function<Class<?>, Boolean> handlerPackage(ScanMatchingRuleEnum scanMatchingRuleEnum, final List<String> basePackages) {
		return input -> {
			// 循环判断匹配
			for (String strPackage : basePackages) {
				boolean isMatch;
				if (ScanMatchingRuleEnum.startsWith.equals(scanMatchingRuleEnum)) {
					isMatch = input.getPackage().getName().startsWith(strPackage);
				}else if (ScanMatchingRuleEnum.endsWith.equals(scanMatchingRuleEnum)) {
					isMatch = input.getPackage().getName().endsWith(strPackage);
				}else  {
					isMatch = input.getPackage().getName().equals(strPackage);
				}
				if (isMatch) {
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * 检验基础包实例
	 *
	 * @param input 请求处理类
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
		return Optional.fromNullable(input.declaringClass());
	}



}
