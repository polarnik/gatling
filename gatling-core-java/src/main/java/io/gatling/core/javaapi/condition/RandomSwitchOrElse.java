/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.core.javaapi.condition;

import io.gatling.core.javaapi.ChainBuilder;
import io.gatling.core.javaapi.Possibility;
import io.gatling.core.javaapi.StructureBuilder;
import io.gatling.core.javaapi.internal.condition.ScalaRandomSwitchOrElse;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * Methods for defining "randomSwitchOrElse" conditional blocks.
 *
 * <p>Important: instances are immutable so any method doesn't mutate the existing instance but
 * returns a new one.
 *
 * @param <T> the type of {@link StructureBuilder} to attach to and to return
 * @param <W> the type of wrapped Scala instance
 */
public interface RandomSwitchOrElse<
    T extends StructureBuilder<T, W>, W extends io.gatling.core.structure.StructureBuilder<W>> {

  T make(Function<W, W> f);

  /**
   * Execute one of the "possibilities" randomly based on their respective weight. Weights are
   * expressed in percents so their sum must be <= 100%.
   *
   * @param possibilities the possibilities with their weight
   * @return the DSL component for defining the "else" block
   */
  @Nonnull
  default OrElse<T> randomSwitchOrElse(@Nonnull Possibility.WithWeight... possibilities) {
    return randomSwitchOrElse(Arrays.asList(possibilities));
  }

  /**
   * Execute one of the "possibilities" randomly based on their respective weight. Weights are
   * expressed in percents so their sum must be <= 100%.
   *
   * @param possibilities the possibilities with their weight
   * @return the DSL component for defining the "else" block
   */
  @Nonnull
  default OrElse<T> randomSwitchOrElse(@Nonnull List<Possibility.WithWeight> possibilities) {
    return new OrElse<>(ScalaRandomSwitchOrElse.apply(this, possibilities));
  }

  /**
   * The DSL component for defining the "else" block
   *
   * @param <T> the type of {@link StructureBuilder} to attach to and to return
   */
  final class OrElse<T extends StructureBuilder<T, ?>> {
    private final ScalaRandomSwitchOrElse.OrElse<T, ?> wrapped;

    OrElse(ScalaRandomSwitchOrElse.OrElse<T, ?> wrapped) {
      this.wrapped = wrapped;
    }

    /**
     * Define the chain to be executed when the random number falls into the gap between 100% and
     * the sum of the weights of the possibilities.
     *
     * @param orElseChain the "then "chain
     * @return a new {@link StructureBuilder}
     */
    @Nonnull
    public T orElse(@Nonnull ChainBuilder orElseChain) {
      return wrapped.orElse(orElseChain);
    }
  }
}