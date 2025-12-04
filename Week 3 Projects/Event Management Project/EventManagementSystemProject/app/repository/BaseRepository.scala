package repository

import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import scala.concurrent.ExecutionContext

abstract class BaseRepository @Inject()(
                                         protected val dbConfigProvider: DatabaseConfigProvider
                                       )(implicit ec: ExecutionContext) {

  protected val dbConfig = dbConfigProvider.get[MySQLProfile]
  protected val db = dbConfig.db
  protected val profile = dbConfig.profile
}
