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

package io.gatling.core.javaapi.internal.condition

import java.{ lang => jl }

import io.gatling.core.javaapi.{ ChainBuilder, StructureBuilder }
import io.gatling.core.javaapi.condition.DoIfOrElse
import io.gatling.core.javaapi.internal.Expressions._
import io.gatling.core.javaapi.internal.JavaExpression
import io.gatling.core.session.Expression
import io.gatling.core.session.el._

object ScalaDoIfOrElse {

  def apply[T <: StructureBuilder[T, W], W <: io.gatling.core.structure.StructureBuilder[W]](context: DoIfOrElse[T, W], condition: String): Then[T, W] =
    new Then(context, condition.el)

  def apply[T <: StructureBuilder[T, W], W <: io.gatling.core.structure.StructureBuilder[W]](
      context: DoIfOrElse[T, W],
      condition: JavaExpression[jl.Boolean]
  ): Then[T, W] =
    new Then(context, javaBooleanFunctionToExpression(condition))

  final class Then[T <: StructureBuilder[T, W], W <: io.gatling.core.structure.StructureBuilder[W]](
      context: DoIfOrElse[T, W],
      condition: Expression[Boolean]
  ) {
    def then_(chain: ChainBuilder): OrElse[T, W] =
      new OrElse(context, condition, chain)
  }

  final class OrElse[T <: StructureBuilder[T, W], W <: io.gatling.core.structure.StructureBuilder[W]](
      context: DoIfOrElse[T, W],
      condition: Expression[Boolean],
      thenChain: ChainBuilder
  ) {
    def orElse(orElseChain: ChainBuilder): T =
      context.make(_.doIfOrElse(condition)(thenChain.wrapped)(orElseChain.wrapped))
  }
}