package jp.sf.amateras.scalaquery.magic

import jp.sf.amateras.scalagen._

/**
 * The source code generator for scalaquery-magic-runtime ORMs.
 * It's possible to generate Table objects and case classes.
 */
class MagicGenerator extends GeneratorBase {
  
  /**
   * Adds TIMESTAMP -> "java.util.Date" to type mappings.
   */
  override def settings(settings: Settings): Settings = {
    settings.copy(typeMappings = DataTypes.defaultMappings + (java.sql.Types.TIMESTAMP -> "java.util.Date"))
  }

  def generate(settings: Settings, table: Table): String = {
    import settings._

    val extendedColumn = (method: String) => (filter: Column => Boolean) =>
      "  def " + method + " = " +
      {table.columns.withFilter{ filter }.map { _.propertyName } match {
        case List(n) => "new SingleMappedProjection(" + n + ", apply_" + method + " _, unapply_" + method + " _)\n"
        case x => x.mkString(" ~ ") + " <> (apply_" + method + " _, unapply_" + method + " _)\n"
      }} +
      "  private def apply_" + method + "(" + table.columns.withFilter{ filter }.map { column => column.propertyName + ": " + propertyType(column) }.mkString(",") + "): " + table.className + " =\n" +
      "    " + table.className + "(" + table.columns.collect {
        case column if !filter(column) => propertyType(column) match {	// default value at primary key
          case "Int" => 0
          case "String" => null
        }
        case column => column.propertyName
      }.mkString(",")  + ")\n" +
      "  private def unapply_" + method + "(o: " + table.className + "): Option[(" + table.columns.withFilter{ filter }.map { propertyType _ }.mkString(",") + ")] =\n" +
      "    Some(" + table.columns.withFilter{ filter }.map { "o." + _.propertyName }.mkString(",")  + ")\n\n"

    // generate code
    {if(packageName == "") "" else "package " + packageName + "\n\n"} +
    "import jp.sf.amateras.scalaquery.magic.{ExtendedTable => Table, _}\n" +
    "import TypeMapper._\n\n" +
    "object " + table.className + "Table extends Table[" + table.className + "](\"" + table.name + "\"){\n" +
    table.columns.map { column =>
      "  def " + column.propertyName + " = column[" + propertyType(column) + "](\"" + column.name + "\")"
    }.mkString("\n") +
    "\n" +
    "  def * = " + table.columns.map { _.propertyName }.mkString(" ~ ") + " <> (" + table.className + ", " + table.className + ".unapply _)\n\n" +
    extendedColumn("+*") { column => !(column.primaryKey && column.typeName.equalsIgnoreCase("serial")) } +
    extendedColumn("**") { !_.primaryKey } +
    "  type PK = " + mkString(table.columns.withFilter(_.primaryKey).map { propertyType _ }){ list => list.mkString("(", ", ", ")") } + "\n" +
    "  def PKColumn = " + table.columns.withFilter(_.primaryKey).map { _.propertyName }.mkString(" ~ ") + "\n" +
    "}\n\n" +
    "case class " + table.className + "(\n" +
    table.columns.map { column => "  " + column.propertyName + ": " + propertyType(column) }.mkString(",\n") +
    "\n) extends GenericEntity {\n" +
    "  type PK = " + table.className + "Table.PK\n" +
    "  def PKValue = " + mkString(table.columns.withFilter(_.primaryKey).map { _.propertyName }){ list => list.mkString("(", ", ", ")") } + "\n" +
    "}\n"
  }
  
  private def propertyType(column: Column) = {
    if(column.nullable) "Option[" + column.dataType + "]" else column.dataType
  }

  private val mkString = (list: List[_]) => (f: List[_] => String) =>
    if(list.size == 1) list.mkString else f(list)
}