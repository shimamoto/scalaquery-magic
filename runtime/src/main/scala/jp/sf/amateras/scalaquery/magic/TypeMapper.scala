package jp.sf.amateras.scalaquery.magic

import org.scalaquery.ql._
import basic.BasicProfile
import org.scalaquery.session._
import org.scalaquery.simple._

/**
 * A (usually implicit) TypeMapper object represents custom types that can be
 * used as a column type in the database.
 */
object TypeMapper {
  /* java.util.Date TypeMapper */
  import java.util.Date
  implicit object DateTypeMapper extends BaseTypeMapper[Date] with TypeMapperDelegate[Date] {
    def apply(p: BasicProfile) = this
    def zero = new Date(0)
    def sqlType = java.sql.Types.TIMESTAMP
    def setValue(v: Date, p: PositionedParameters) = p.setTimestamp(new java.sql.Timestamp(v.getTime))
    def setOption(v: Option[Date], p: PositionedParameters) = p.setTimestampOption(v.map(d => new java.sql.Timestamp(d.getTime)))
    def nextValue(r: PositionedResult) = r.nextTimestamp
    def updateValue(v: Date, r: PositionedResult) = r.updateTimestamp(new java.sql.Timestamp(v.getTime))
    override def valueToSQLLiteral(value: Date) = "{date '"+value.toString+"'}"
  }

  implicit object GetJavaDate extends GetResult[Date] { def apply(rs: PositionedResult) = new Date(rs.nextTimestamp().getTime) }
  implicit object GetJavaDateOption extends GetResult[Option[Date]] { def apply(rs: PositionedResult) = rs.nextTimestampOption() match {
    case Some(t) => Some(new Date(t.getTime))
    case None => None
  }}

}