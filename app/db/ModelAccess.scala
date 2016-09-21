package db

import db.ModelFilter.IdFilter
import db.impl.pg.OrePostgresDriver.api._
import slick.lifted.ColumnOrdered

/**
  * Provides simple, synchronous, access to a ModelTable.
  */
class ModelAccess[T <: ModelTable[M], M <: Model](val service: ModelService,
                                                  val modelClass: Class[M],
                                                  val baseFilter: ModelFilter[T, M] = ModelFilter[T, M]()) {

  /** Model filter alias */
  type Filter = T => Rep[Boolean]
  type Ordering = T => ColumnOrdered[_]

  private val actions: ModelActions[T, M] = this.service.registry.getActionsByModel[T, M](this.modelClass)

  /**
    * Returns the model with the specified ID.
    *
    * @param id   ID to lookup
    * @return     Model with ID or None if not found
    */
  def get(id: Int): Option[M] = this.service.await(this.actions.get(id, this.baseFilter.fn)).get

  /**
    * Returns all the [[Model]]s in the set.
    *
    * @return All models in set
    */
  def all: Set[M] = this.service.await(this.actions.filter(this.baseFilter)).get.toSet

  /**
    * Returns the size of this set.
    *
    * @return Size of set
    */
  def size: Int = this.service.await(this.actions count this.baseFilter).get

  /**
    * Returns true if this set is empty.
    *
    * @return True if set is empty
    */
  def isEmpty: Boolean = this.size == 0

  /**
    * Returns true if this set is not empty.
    *
    * @return True if not empty
    */
  def nonEmpty: Boolean = this.size > 0

  /**
    * Returns true if this set contains the specified model.
    *
    * @param model Model to look for
    * @return True if contained in set
    */
  def contains(model: M): Boolean
  = this.service.await(this.actions count (this.baseFilter +&& IdFilter(model.id.get))).get > 0

  /**
    * Returns true if any models match the specified filter.
    *
    * @param filter Filter to use
    * @return       True if any model matches
    */
  def exists(filter: Filter) = this.service.await(this.actions count (this.baseFilter && filter)).get > 0

  /**
    * Adds a new model to it's table.
    *
    * @param model Model to add
    * @return New model
    */
  def add(model: M): M = this.service.await(this.actions insert model).get

  /**
    * Removes the specified model from this set if it is contained.
    *
    * @param model Model to remove
    */
  def remove(model: M) = this.service.await(this.actions delete model).get

  /**
    * Removes all the models from this set matching the given filter.
    *
    * @param filter Filter to use
    */
  def removeAll(filter: Filter = _ => true) = this.service.await(this.actions deleteWhere (this.baseFilter && filter))

  /**
    * Returns the first model matching the specified filter.
    *
    * @param filter Filter to use
    * @return       Model matching filter, if any
    */
  def find(filter: Filter): Option[M] = this.service.await(this.actions.find(this.baseFilter && filter)).get

  /**
    * Returns a sorted Seq by the specified [[ColumnOrdered]].
    *
    * @param ordering Model ordering
    * @param filter   Filter to use
    * @param limit    Amount to take
    * @param offset   Amount to drop
    * @return         Sorted models
    */
  def sorted(ordering: Ordering, filter: Filter = null, limit: Int = -1, offset: Int = -1): Seq[M]
  = this.service.await(this.actions.sorted(ordering, this.baseFilter && filter, limit, offset)).get

  /**
    * Filters this set by the given function.
    *
    * @param filter Filter to use
    * @param limit  Amount to take
    * @param offset Amount to drop
    * @return       Filtered models
    */
  def filter(filter: Filter, limit: Int = -1, offset: Int = -1): Seq[M]
  = this.service.await(this.actions.filter(this.baseFilter && filter, limit, offset)).get

  /**
    * Filters this set by the opposite of the given function.
    *
    * @param filter Filter to use
    * @param limit  Amount to take
    * @param offset Amount to drop
    * @return       Filtered models
    */
  def filterNot(filter: Filter, limit: Int = -1, offset: Int = -1): Seq[M] = this.filter(!filter(_), limit, offset)

  /**
    * Returns a Seq of this set.
    *
    * @return Seq of set
    */
  def toSeq: Seq[M] = this.all.toSeq

}