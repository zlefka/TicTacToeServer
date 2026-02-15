package di

import domain.repository.GameRepository
import datasource.repository.GameRepositoryImpl
import domain.service.GameService
import domain.service.MinimaxService
import org.koin.dsl.module

val gameModule = module {
    single<GameRepository> { GameRepositoryImpl() }
    single<GameService> { MinimaxService(get()) }
}