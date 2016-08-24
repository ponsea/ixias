/*
 * This file is part of the IxiaS services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package ixias.persistence.lifted

import java.sql._
import java.util.Calendar
import scala.language.reflectiveCalls
import scala.language.implicitConversions

import slick.driver.JdbcProfile
import slick.jdbc.{ SetParameter, PositionedParameters, GetResult, PositionedResult }

sealed case class SlickColumnTypesExtension[P <: JdbcProfile](val driver: P)
{
  /** org.joda.time.DateTime */
  object JodaDateTime {
    type T1 = org.joda.time.DateTime
    type T2 = java.sql.Timestamp

    protected def toT1(v: T2): T1 = if (v == null) null else new T1(v.getTime)
    protected def toT2(v: T1): T2 = if (v == null) null else new T2(v.getMillis)
    protected def toCalendar(v: T1): Calendar = Calendar.getInstance(v.getZone().toTimeZone())

    object Type extends driver.DriverJdbcType[T1] {
      def zero = new T1(0L)
      def sqlType = java.sql.Types.TIMESTAMP

      def    getValue(       r: ResultSet,         idx: Int): T1   = toT1(r.getTimestamp(idx))
      def updateValue(v: T1, r: ResultSet,         idx: Int): Unit = r.updateTimestamp(idx, toT2(v))
      def    setValue(v: T1, p: PreparedStatement, idx: Int): Unit = p.setTimestamp(idx, toT2(v), toCalendar(v))
      override def valueToSQLLiteral(value: T1): String = "{ts '" + toT2(value).toString + "'}"
    }
  }
}

trait SlickColumnTypeOps[P <: JdbcProfile] {
  val driver: P
  val columnTypes = SlickColumnTypesExtension(driver)
  implicit val jodaDateTimeColumnType  = columnTypes.JodaDateTime.Type
}
