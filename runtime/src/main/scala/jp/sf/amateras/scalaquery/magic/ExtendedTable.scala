package jp.sf.amateras.scalaquery.magic

import org.scalaquery.ql._
import basic.BasicTable

/**
 * Base class for a table object.
 * 
 * The table object extending this class can be used the basic operation 
 * of [[jp.sf.amateras.scalaquery.magic.MagicDao]].
 * 
 * @author Takako Shimamoto
 * @version 1.0
 * @since   1.0
 * @param _tableName the table name
 * @tparam R the entity type mapped at this table
 */
abstract class ExtendedTable[R](_tableName: String) extends BasicTable[R](_tableName) {
  /**
   * Returns the insert columns which used at the insert operation of DAO trait.
   */
  def +* : ColumnBase[R]

  /**
   * Returns the update columns which used at the update operation of DAO trait.
   */
  def ** : ColumnBase[R]

  /**
   * primary key type.
   */
  type PK

  /**
   * Returns the primary key columns.
   */
  def PKColumn : ColumnBase[PK]
}

/**
 * Base class for a entity.
 */
abstract class GenericEntity {
  /**
   * primary key type.
   */
  type PK

  /**
   * Returns the primary key values.
   */
  def PKValue: PK
}