/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.morpheus.engine

package object query {
  sealed trait GroupBind
  final abstract class Groupped extends GroupBind
  final abstract class Ungroupped extends GroupBind

  sealed trait ChainBind
  final abstract class Chainned extends ChainBind
  final abstract class Unchainned extends ChainBind

  sealed trait OrderBind
  final abstract class Ordered extends OrderBind
  final abstract class Unordered extends OrderBind

  sealed trait LimitBind
  final abstract class Limited extends LimitBind
  final abstract class Unlimited extends LimitBind

}
