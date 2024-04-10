package com.nshmura.bbs.domain.model.user

import com.nshmura.bbs.domain.model.user.values.UserId
import com.nshmura.bbs.domain.model.user.values.UserName

/**
 * ユーザ
 */
class User(
    val id: UserId,
    val name: UserName
)
