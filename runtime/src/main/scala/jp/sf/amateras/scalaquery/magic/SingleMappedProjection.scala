package jp.sf.amateras.scalaquery.magic

import org.scalaquery.ql.ColumnBase
import org.scalaquery.ql.basic.BasicProfile
import org.scalaquery.session.{PositionedResult, PositionedParameters}
import org.scalaquery.util.{Node, NullaryNode}

/**
 * Projection class represents 1 column mapping.
 */
class SingleMappedProjection[T, P](column: ColumnBase[P], f: (P => T), g: (T => Option[P])) extends ColumnBase[T] with NullaryNode {
  override def nodeDelegate = Node(column)
  def setParameter(profile: BasicProfile, ps: PositionedParameters, value: Option[T]): Unit =
    column.setParameter(profile, ps, value.flatMap(g))
  def getResult(profile: BasicProfile, rs: PositionedResult) = f(column.getResult(profile, rs))
  def updateResult(profile: BasicProfile, rs: PositionedResult, value: T) = column.updateResult(profile, rs, g(value).get)
}