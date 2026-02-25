package di

import domain.repository.GameRepository
import datasource.repository.GameRepositoryImpl
import datasource.repository.UserRepositoryImpl
import domain.repository.UserRepository
import domain.service.GameService
import domain.service.MinimaxComputerService
import domain.service.UserService
import org.koin.dsl.module

val gameModule = module {
    single<GameRepository> { GameRepositoryImpl() }
    single<GameService> { MinimaxComputerService(get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserService(get()) }
}