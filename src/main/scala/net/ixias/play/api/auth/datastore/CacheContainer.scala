/*
 * This file is part of the IxiaS services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package net.ixias
package play.api.auth.datastore

import _root_.play.api.Play._
import _root_.play.api.cache.Cache

import play.api.auth.token._
import core.domain.model.Identity

/* The datastore for user session with using `play.cache.Cache` */
case class CacheContainer[Id <: Identity[_]]() extends Container[Id] {

  /** The suffix as string of the cache-key */
  protected val prefix = "sid:"

  /** It is the first callback function executed
    * when the session is started automatically or manually */
  def open(uid: Id, expiry: Int): AuthenticityToken = {
    val token = Token.generate(this)
    Cache.set(prefix + token, uid, expiry)
    token
  }

  /** The read callback must always return
    * a user identity or none if there is no data to read */
  def read(token: AuthenticityToken): Option[Id] =
    Cache.get(prefix + token).map(_.asInstanceOf[Id])

  /** This callback is executed when a session is destroyed */
  def destroy(token: AuthenticityToken): Unit =
    Cache.remove(prefix + token)

  /** Sets the timeout setting. */
  def setTimeout(token: AuthenticityToken, expiry: Int): Unit =
    read(token).foreach(Cache.set(prefix + token, _, expiry))

}
