/*
 * Copyright 2017 Petra Bierleutgeb
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

package io.ontherocks.hellogrpc

import com.trueaccord.scalapb.TypeMapper

import scala.util.Try

final case class Birthday(month: Int, day: Int)

/**
  * Demoes how to create a type mapper.
  */
object Birthday {

  def base2custom(birthdayStr: String): Birthday =
    Try {
      val b = birthdayStr.split("-")
      Birthday(Integer.parseInt(b(0)), Integer.parseInt(b(1)))
    }.getOrElse(Birthday(1, 1))

  def custom2base(birthday: Birthday): String = s"${birthday.month}-${birthday.day}"

  implicit val typeMapper: TypeMapper[String, Birthday] = TypeMapper(base2custom)(custom2base)

}
