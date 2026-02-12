package di

import datasource.repository.GameRepository
import datasource.repository.MemoryGameRepository
import domain.service.GameService
import domain.service.MinimaxService
import org.koin.dsl.module

val gameModule = module {
    single<GameRepository> { MemoryGameRepository() }
    single<GameService> { MinimaxService(get()) }
}