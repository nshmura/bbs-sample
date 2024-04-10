package com.nshmura.bbs.infrastructure.persistence.users

import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.values.UserId
import com.nshmura.bbs.domain.model.user.values.UserName
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface UserDBMapper {

    @Select(
        """
        SELECT `id`
             , `name`
          FROM `users`
         WHERE `id` = #{id}
        """
    )
    fun selectUserById(id: String): UserData?

    @Select(
        """
        <script>
        SELECT `id`
             , `name`
          FROM `users`
         WHERE `id` IN
        <foreach item="item" index="index" collection="ids" open="(" separator="," close=")">
            #{item}
        </foreach>
        </script>
        """
    )
    fun selectUsersByIds(ids: List<String>): List<UserData>

    @Select(
        """
        SELECT `id`
             , `name`
          FROM `users`
        WHERE `deleted_at` IS NULL
        """
    )
    fun selectAll(): List<UserData>
}

data class UserData(
    val id: String,
    val name: String
) {
    fun toModel(): User {
        return User(
            id = UserId.of(id),
            name = UserName.of(name)
        )
    }
}
