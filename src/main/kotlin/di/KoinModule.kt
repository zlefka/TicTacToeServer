package di

import domain.repository.GameRepository
import datasource.repository.GameRepositoryImpl
import datasource.repository.UserRepositoryImpl
import domain.repository.UserRepository
import domain.service.GameService
import domain.service.MinimaxComputerService
import domain.service.TwoPlayersService
import domain.service.UserService
import org.koin.core.qualifier.named
import org.koin.dsl.module
import web.security.AuthService
import web.security.JwtProvider

val gameModule = module {
    single<GameRepository> { GameRepositoryImpl() }
    single<GameService>(qualifier = named("computer")) { MinimaxComputerService(get()) }
    single<GameService>(qualifier = named("twoPlayers")) { TwoPlayersService(get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserService(get()) }
    single {
        JwtProvider(
            secret = getProperty("jwt.secret"),
            audience = getProperty("jwt.audience"),
            issuer = getProperty("jwt.issuer"),
            realm = getProperty("jwt.realm"),
            accessTokenValidity = getProperty("jwt.accessTokenValidity"),
            refreshTokenValidity = getProperty("jwt.refreshTokenValidity")
        )
    }
    single<AuthService> { AuthService(get(), get()) }
}