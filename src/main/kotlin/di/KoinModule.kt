package di

import domain.repository.GameRepository
import datasource.repository.GameRepositoryImpl
import datasource.repository.UserRepositoryImpl
import domain.model.User
import domain.repository.UserRepository
import domain.service.GameService
import domain.service.MinimaxComputerService
import domain.service.UserService
import org.koin.dsl.module
import kotlin.math.sin

val gameModule = module {
    single<GameRepository> { GameRepositoryImpl() } // singleton репозиторий
    single<GameService> { MinimaxComputerService(get()) }     // singleton сервис, получает репозиторий
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserService(get()) }
}