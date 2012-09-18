package jp.sf.amateras.scalaquery.magic

import org.scalaquery.ql._

/**
 * A implicit conversion trait for a column which is part of a Table.
 */
trait ColumnOps {
  /**
   * ambiguous retrieval.
   */
  implicit def valueContains(column: NamedColumn[String]) = new {
    def contains[R](s: String)(implicit om: OptionMapper2[String, String, Boolean, String, String, R]): Column[R] =
      om(ColumnOps.Like(column, ConstColumn('%' + ColumnOps.likeEncode(s) + '%'), Some('^')))
  }

  implicit def optionValueContains(column: NamedColumn[Option[String]]) = new {
    def contains[R](s: String)(implicit om: OptionMapper2[String, String, Boolean, Option[String], Option[String], R]): Column[R] =
      om(ColumnOps.Like(column, ConstColumn('%' + ColumnOps.likeEncode(s) + '%'), Some('^')))
  }

}