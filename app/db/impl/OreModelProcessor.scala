package db.impl

import db.impl.access.{OrganizationBase, ProjectBase, UserBase}
import db.meta.ModelProcessor
import db.{Model, ModelService, ModelTable}
import forums.DiscourseApi
import ore.OreConfig

import scala.reflect.runtime.universe._

/**
  * A ModelProcessor that performs Ore specific model processing.
  *
  * @param service  ModelService
  * @param config   Ore config
  */
class OreModelProcessor(service: ModelService,
                        users: UserBase,
                        projects: ProjectBase,
                        organizations: OrganizationBase,
                        config: OreConfig,
                        forums: DiscourseApi)
                        extends ModelProcessor(service) {

  override def process[T <: ModelTable[M], M <: Model: TypeTag](model: M) = {
    super.process(model) match {
      case oreModel: OreModel =>
        oreModel.userBase = this.users
        oreModel.projectBase = this.projects
        oreModel.organizationBase = this.organizations
        oreModel.config = this.config
        oreModel.forums = this.forums
      case _ =>
    }
    model
  }
}