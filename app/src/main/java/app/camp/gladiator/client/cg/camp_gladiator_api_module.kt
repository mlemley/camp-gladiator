package app.camp.gladiator.client.cg

import org.koin.dsl.module

val campGladiatorApiModule = module {
    factory { CampGladiatorApiFactory.campGladiatorApiClient() }
}