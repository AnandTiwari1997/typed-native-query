package open.source.typednativequery.expressions;

import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.core.CustomQueryParameter;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NamedQueryParameter;
import open.source.typednativequery.core.NativeSubQuery;
import open.source.typednativequery.function.Function;
import open.source.typednativequery.function.string.*;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.LikeOperator;

/**
 * The interface String function expression.
 *
 * @param <T> the type parameter
 */
public interface StringFunctionExpression<T extends Table> extends IQuery {

  /**
   * Like like operator.
   *
   * @param pattern the pattern
   * @return the like operator
   */
  default LikeOperator like(String pattern) {
    return new LikeOperator(this, new CustomQueryParameter<>(pattern));
  }

  /**
   * Like like operator.
   *
   * @param <X> the type parameter
   * @param nativeQueryColumn the native query column
   * @return the like operator
   */
  default <X extends Table> LikeOperator like(NativeQueryColumn<X, String> nativeQueryColumn) {
    return new LikeOperator(this, nativeQueryColumn);
  }

  /**
   * Like like operator.
   *
   * @param <X> the type parameter
   * @param nativeSubQuery the native sub query
   * @return the like operator
   */
  default <X extends Table> LikeOperator like(NativeSubQuery<X, ?> nativeSubQuery) {
    return new LikeOperator(this, nativeSubQuery);
  }

  /**
   * Like like operator.
   *
   * @param customQueryParameter the custom query parameter
   * @return the like operator
   */
  default LikeOperator like(CustomQueryParameter<String> customQueryParameter) {
    return new LikeOperator(this, customQueryParameter);
  }

  /**
   * Like like operator.
   *
   * @param namedQueryParameter the named query parameter
   * @return the like operator
   */
  default LikeOperator like(NamedQueryParameter namedQueryParameter) {
    return new LikeOperator(this, namedQueryParameter);
  }

  /**
   * Not like like operator.
   *
   * @param pattern the pattern
   * @return the like operator
   */
  default LikeOperator notLike(String pattern) {
    return like(pattern).not();
  }

  /**
   * Not like like operator.
   *
   * @param <X> the type parameter
   * @param nativeQueryColumn the native query column
   * @return the like operator
   */
  default <X extends Table> LikeOperator notLike(NativeQueryColumn<X, String> nativeQueryColumn) {
    return like(nativeQueryColumn).not();
  }

  /**
   * Not like like operator.
   *
   * @param <X> the type parameter
   * @param nativeSubQuery the native sub query
   * @return the like operator
   */
  default <X extends Table> LikeOperator notLike(NativeSubQuery<X, ?> nativeSubQuery) {
    return like(nativeSubQuery).not();
  }

  /**
   * Not like like operator.
   *
   * @param customQueryParameter the custom query parameter
   * @return the like operator
   */
  default LikeOperator notLike(CustomQueryParameter<String> customQueryParameter) {
    return like(customQueryParameter).not();
  }

  /**
   * Not like like operator.
   *
   * @param namedQueryParameter the named query parameter
   * @return the like operator
   */
  default LikeOperator notLike(NamedQueryParameter namedQueryParameter) {
    return like(namedQueryParameter).not();
  }

  /**
   * Concat concat function.
   *
   * @param pattern the pattern
   * @return the concat function
   */
  default ConcatFunction<T> concat(String pattern) {
    return new ConcatFunction<>(this, new CustomQueryParameter<>(pattern));
  }

  /**
   * Concat concat function.
   *
   * @param pattern the pattern
   * @return the concat function
   */
  default ConcatFunction<T> concat(CustomQueryParameter<String> pattern) {
    return new ConcatFunction<>(this, pattern);
  }

  /**
   * Concat concat function.
   *
   * @param <X> the type parameter
   * @param nativeQueryColumn the native query column
   * @return the concat function
   */
  default <X extends Table> ConcatFunction<X> concat(
      NativeQueryColumn<X, String> nativeQueryColumn) {
    return new ConcatFunction<>(this, nativeQueryColumn);
  }

  /**
   * Concat concat function.
   *
   * @param <X> the type parameter
   * @param nativeQueryColumn the native query column
   * @return the concat function
   */
  default <X extends Table> ConcatFunction<X> concat(Function<X, ?, String> nativeQueryColumn) {
    return new ConcatFunction<>(this, nativeQueryColumn);
  }

  /**
   * Substring sub str function.
   *
   * @param len the len
   * @return the sub str function
   */
  default SubStrFunction<T> substring(int len) {
    return new SubStrFunction<>(this, -1, len);
  }

  /**
   * Substring sub str function.
   *
   * @param from the from
   * @param len the len
   * @return the sub str function
   */
  default SubStrFunction<T> substring(int from, int len) {
    return new SubStrFunction<>(this, from, len);
  }

  /** Used to specify how strings are trimmed. */
  enum TrimType {
    /** Leading trim type. */
    LEADING,
    /** Trailing trim type. */
    TRAILING,
    /** Both trim type. */
    BOTH
  }

  /**
   * Trim trim function.
   *
   * @return the trim function
   */
  default TrimFunction<T> trim() {
    return new TrimFunction<>(this);
  }

  /**
   * Trim trim function.
   *
   * @param trimType the trim type
   * @return the trim function
   */
  default TrimFunction<T> trim(TrimType trimType) {
    return new TrimFunction<>(this, trimType);
  }

  /**
   * Trim trim function.
   *
   * @param t the t
   * @return the trim function
   */
  default TrimFunction<T> trim(char t) {
    return new TrimFunction<>(this, new CustomQueryParameter<>(t));
  }

  /**
   * Trim trim function.
   *
   * @param trimType the trim type
   * @param t the t
   * @return the trim function
   */
  default TrimFunction<T> trim(TrimType trimType, char t) {
    return new TrimFunction<>(this, new CustomQueryParameter<>(t), trimType);
  }

  /**
   * Lower lower case function.
   *
   * @return the lower case function
   */
  default LowerCaseFunction<T> lower() {
    return new LowerCaseFunction<>(this);
  }

  /**
   * Upper upper case function.
   *
   * @return the upper case function
   */
  default UpperCaseFunction<T> upper() {
    return new UpperCaseFunction<>(this);
  }

  /**
   * Length length function.
   *
   * @return the length function
   */
  default LengthFunction<T> length() {
    return new LengthFunction<>(this);
  }

  /**
   * Locate.
   *
   * @param pattern the pattern
   */
  default void locate(String pattern) {}

  /**
   * Locate.
   *
   * @param pattern the pattern
   * @param from the from
   */
  default void locate(String pattern, int from) {}
}
