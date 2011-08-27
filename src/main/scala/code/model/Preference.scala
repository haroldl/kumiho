/* * Copyright 2011 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package code.model

import net.liftweb.mapper.By
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedLongIndex
import net.liftweb.mapper.MappedString

import code.comet.LogServer

class Preference extends LongKeyedMapper[Preference] {

  def getSingleton = Preference

  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 40)
  object value extends MappedString(this, 40)
}

object Preference extends Preference with LongKeyedMetaMapper[Preference] {

  val maxRecords = "maxRecords"

  def getValue(name: String): Option[Preference] = {
    val values = Preference.findAll(By(Preference.name, name))
    if (values.size > 0) Some(values(0)) else None
  }

  def setValue(name: String, value: String) = {
    val p = Preference.create
    p.name(name)
    p.value(value)
    p.save()
    LogServer ! p
  }

}
