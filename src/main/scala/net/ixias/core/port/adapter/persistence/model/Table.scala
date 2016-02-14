/*
 * This file is part of the IxiaS services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package net.ixias
package core.port.adapter.persistence.model

import slick.driver.JdbcProfile
import core.port.adapter.persistence.lifted._

trait Table[R, P <: JdbcProfile] { self =>

  //-- [ Required properties ] -------------------------------------------------
  /** The configured driver. */
  val driver: P

  /** The map of DSN as string. */
  val dsn: Map[String, DataSourceName]

  /** The table query. */
  val query: Query

  //-- [ Table Manifest ] ------------------------------------------------------
  /** The type of table row. */
  type Record = R

  /** A Tag marks a specific row represented by an AbstractTable instance. */
  type Tag = slick.lifted.Tag

  /** The type of all table row objects. */
  type Table      <: driver.Table[Record]
  type BasicTable =  driver.Table[Record]

  //-- [ Table Query ] ---------------------------------------------------------
  /** Represents a database table. Implementation class add extension methods to TableQuery
    * for operations that can be performed on tables but not on arbitrary queries. */
  type Query      <: slick.lifted.TableQuery[Table]
  type BasicQuery =  slick.lifted.TableQuery[Table]

  //-- [ Utility Methods ] -----------------------------------------------------
  /** The API for using the utility methods with a single import statement.
    * This provides the repository's implicits, the Database connections,
    * and commonly types and objects. */
  trait API extends driver.API
      with SlickColumnOptionOps
      with SlickColumnTypeOps[P] {
    lazy val driver = self.driver
  }
  val api: API = new API {}
}
