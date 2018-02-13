package models.admin

import java.sql.Timestamp

import db.Model
import db.impl.ChangeRequestTable
import db.impl.model._
import db.impl.table.ModelKeys._
import models.project.Page
import models.user.User
import play.twirl.api.Html

/**
  * Represents
  *
  * @param id
  * @param createdAt
  * @param projectId
  * @param comment
  * @param createdBy
  * @param resolvedAt
  */
case class ChangeRequest(override val id: Option[Int] = None,
                         override val createdAt: Option[Timestamp] = None,
                         projectId: Int = -1,
                         comment: String,
                         createdBy: Int = -1,
                         var resolvedAt: Option[Timestamp] = None,
                         var resolvedBy: Option[Int] = None) extends OreModel(id, createdAt) {

  /** Self referential type */
  override type M = ChangeRequest
  /** The model's table */
  override type T = ChangeRequestTable

  def render(): Html = Page.Render(comment)

  def isResolved: Boolean = resolvedBy.isEmpty

  def setResolvedAt(time: Timestamp) = {
    this.resolvedAt = Some(time)
    update(ResolvedAtCQ)
  }

  def setResolvedBy(user: User) = {
    this.resolvedBy = user.id
    update(ResolvedByCQ)
  }

  /**
    * Returns a copy of this model with an updated ID and timestamp.
    *
    * @param id      ID to set
    * @param theTime Timestamp
    * @return Copy of model
    */
  override def copyWith(id: Option[Int], theTime: Option[Timestamp]): Model = this.copy(id = id, createdAt = createdAt)
}
