package jp.sf.amateras.scalaquery.magic

import org.scalaquery.ql._

/**
 * A implicit conversion trait for a column which is part of a Table.
 */
trait ColumnOps {
  /**
   * ambiguous retrieval.
   */
  implicit def columnOpsContains(column: NamedColumn[String]) = new {
    def contains[R](s: String)(implicit om: OptionMapper2[String, String, Boolean, String, String, R]): Column[R] =
      om(ColumnOps.Like(column, ConstColumn('%' + ColumnOps.likeEncode(s) + '%'), Some('^')))
  }

}