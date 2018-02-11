package models.project

import db.impl.OrePostgresDriver
import db.table.MappedType
import slick.jdbc.JdbcType


object VisibilityTypes extends Enumeration {
  val Public          = Visibility(1, "public"        , "")
  val New             = Visibility(2, "new"           , "project-new")
  val NeedsChanges    = Visibility(3, "needsChanges"  , "striped project-needsChanges")
  val NeedsApproval   = Visibility(4, "needsApproval" , "striped project-needsChanges")
  val SoftDelete      = Visibility(5, "softDelete"    , "striped project-hidden")

  def withId(id: Int): Visibility = {
    this.apply(id).asInstanceOf[Visibility]
  }

  case class Visibility(override val id: Int, nameKey: String, cssClass: String) extends super.Val(id) with MappedType[Visibility] {
    implicit val mapper: JdbcType[Visibility] = OrePostgresDriver.api.visibiltyTypeMapper
  }

  implicit def convert(value: Value): Visibility = value.asInstanceOf[Visibility]
}
