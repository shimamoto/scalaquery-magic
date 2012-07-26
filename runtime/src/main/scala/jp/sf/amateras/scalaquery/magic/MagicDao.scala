package jp.sf.amateras.scalaquery.magic

import org.scalaquery.ql._
import basic.BasicDriver.Implicit._
import org.scalaquery.util.Node
import org.scalaquery.session.Database.threadLocalSession
import scala.annotation.tailrec

/**
 * Base trait for all data access objects.
 * 
 * Data access objects mixing in this trait provide following operations:
 *   - Select by primary key
 *   - Insert a single data
 *   - Update by primary key
 *   - Delete by primary key
 * 
 * @author Takako Shimamoto
 * @version 1.0
 * @since   1.0
 * @tparam R the result type of statement invocation
 */
trait MagicDao[R <: GenericEntity] {
  val table: ExtendedTable[R]

  /**
   * Returns the first row value.
   * 
   * @param id primary key
   * @return an option value containing the first row
   */
  def selectById(id: table.PK): Option[R] = {
    implicit val f = addBindVariables	// build query by bind variables

    val sql = table.where { t =>
      id match {
        case v: Product => setConditions(t.PKColumn.nodeChildren, v)
        case v => setCondition(t.PKColumn.nodeDelegate, v)
      }
    }
    sql(id).firstOption
  }

  /**
   * Insert a single row.
   * 
   * @param o entity data
   * @return results row count
   */
  def insert(o: R): Int = {
    table.+* insert o
  }

  /**
   * Update a single row.
   * 
   * @param o entity data
   * @return results row count
   */
  def update(o: R): Int = {
    implicit val f = addVariables	// compiled with every execution

    val sql = table.where { t =>
      o.PKValue match {
        case v: Product => setConditions(t.PKColumn.nodeChildren, v)
        case v => setCondition(t.PKColumn.nodeDelegate, v)
      }
    }.map(_.**)
    sql.update(o)
  }

  /**
   * Delete the single row.
   * 
   * @param id primary key
   * @return results row count
   */
  def deleteById(id: table.PK): Int = {
    implicit val f = addVariables	// compiled with every execution

    val sql = table.where { t =>
      id match {
        case v: Product => setConditions(t.PKColumn.nodeChildren, v)
        case v => setCondition(t.PKColumn.nodeDelegate, v)
      }
    }
    sql.delete
  }

  /**
   * Build where condition of multiple primary key.
   * 
   * @param cols [[org.scalaquery.ql.Column]] object list of primary key
   * @param id primary key
   * @param cond built conditions
   * @param i current primary key position
   * @param add a function which build each condition
   */
  @tailrec
  private def setConditions(
      cols :List[Node],
      id: Product,
      cond: Column[Boolean] = null,
      i: Int = 0)(implicit add: (Any, ColumnBase[_]) => Column[Boolean]): Column[Boolean] =
    if(cols.isEmpty) cond
    else if(cond == null) setConditions(cols.tail, id,
        add(id.productElement(i), cols.head.asInstanceOf[ColumnBase[_]]), i + 1)
    else setConditions(cols.tail, id,
        cond && add(id.productElement(i), cols.head.asInstanceOf[ColumnBase[_]]), i + 1)

  /**
   * Build where condition of single primary key.
   * 
   * @param col [[org.scalaquery.ql.Column]] object of primary key
   * @param id primary key
   * @param add a function which build single condition
   */
  private def setCondition(
      col: Node,
      id: Any)(implicit add: (Any, ColumnBase[_]) => Column[Boolean]): Column[Boolean] =
    add(id, col.asInstanceOf[ColumnBase[_]])

  /**
   * The function which build query by bind variables.
   */
  private val addBindVariables = (element: Any, col: ColumnBase[_]) =>
    element match {
      case v: Int => col.asInstanceOf[Column[Int]] is v.bind
      case v: String => col.asInstanceOf[Column[String]] is v.bind
    }
  /**
   * The function which compiled with every execution.
   * See https://github.com/slick/slick/issues/17
   */
  private val addVariables = (element: Any, col: ColumnBase[_]) =>
    element match {
      case v: Int => col.asInstanceOf[Column[Int]] is v
      case v: String => col.asInstanceOf[Column[String]] is v
    }
}