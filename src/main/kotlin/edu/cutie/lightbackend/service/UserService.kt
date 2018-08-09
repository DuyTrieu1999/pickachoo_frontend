package edu.cutie.lightbackend.service

import edu.cutie.lightbackend.data
import edu.cutie.lightbackend.domain.PersonEntity

interface UserService {
  fun findByFacebookId(id: Long): PersonEntity?
}

class DefaultUserService : UserService {
  override fun findByFacebookId(id: Long): PersonEntity? =
    data.select(PersonEntity::class).where(PersonEntity.FB_ID.eq(id)).get().firstOrNull()
}
