package app.camp.gladiator.client.google

import org.koin.core.qualifier.named
import org.koin.dsl.module

val google_api_module = module {
    factory { GoogleApiFactory.googleApiClient(get(named("GoogleApiKey"))) }
}