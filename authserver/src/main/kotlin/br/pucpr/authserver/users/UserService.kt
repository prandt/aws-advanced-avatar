package br.pucpr.authserver.users

import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val jwt: Jwt,
    val avatarService: AvatarService
) {

    fun save(user: User): User =
        avatarService
            .getAvatarUrl(user.email, user.name)
            .run { user.avatar = this }
            .run { userRepository.save(user) }

    fun findAll(dir: SortDir, role: String?) =
        role?.let { r ->
            when (dir) {
                SortDir.ASC -> userRepository.findByRole(r.uppercase()).sortedBy { it.name }
                SortDir.DESC -> userRepository.findByRole(r.uppercase()).sortedByDescending { it.name }
            }
        } ?: when (dir) {
            SortDir.ASC -> userRepository.findAll(Sort.by("name").ascending())
            SortDir.DESC -> userRepository.findAll(Sort.by("name").descending())
        }

    fun findByIdOrNull(id: Long) = userRepository.findByIdOrNull(id)

    fun delete(id: Long) = userRepository.findByIdOrNull(id)
            .also { userRepository.deleteById(id) }

    fun addRole(id: Long, roleName: String): Boolean {
        val user = userRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("User $id not found!")

        if (user.roles.any { it.name == roleName }) return false

        val role = roleRepository.findByName(roleName)
            ?: throw IllegalArgumentException("Invalid role $roleName!")

        user.roles.add(role)
        userRepository.save(user)
        return true
    }

    fun login(email: String, password: String): LoginResponse? {
        val user = userRepository.findByEmail(email).firstOrNull()

        if (user == null) {
            log.warn("User {} not found!", email)
            return null
        }
        if (password != user.password) {
            log.warn("Invalid password!")
            return null
        }
        log.info("User logged in: id={}, name={}", user.id, user.name)
        return LoginResponse(
            token = jwt.createToken(user),
            UserResponse(user)
        )
    }

    fun deleteAvatar(id: Long): User? {
        val user = userRepository.findByIdOrNull(id) ?: return null
        return avatarService
            .getAvatarUrl(user.email, user.name)
            .run { user.avatar = this }
            .run { userRepository.save(user) }
    }

    companion object {
        val log = LoggerFactory.getLogger(UserService::class.java)
    }
}
