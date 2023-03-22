package herbaccara.boot.autoconfigure.aligo.talk

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.aligo.talk.AligoTalkService
import herbaccara.aligo.talk.store.AligoTalkInMemoryTokenStore
import herbaccara.aligo.talk.store.AligoTalkTokenStore
import herbaccara.boot.autoconfigure.aligo.AligoProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
import java.nio.charset.StandardCharsets
import java.util.*

@AutoConfiguration
@EnableConfigurationProperties(AligoProperties::class)
@ConditionalOnProperty(prefix = "aligo.talk", value = ["enabled"], havingValue = "true")
class AligoTalkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun aligoTalkTokenStore(): AligoTalkTokenStore {
        return AligoTalkInMemoryTokenStore()
    }

    @Bean
    @ConditionalOnMissingBean
    fun aligoTalkService(
        customizers: List<AligoTalkRestTemplateBuilderCustomizer>,
        interceptors: List<AligoTalkClientHttpRequestInterceptor>,
        properties: AligoProperties,
        tokenStore: AligoTalkTokenStore
    ): AligoTalkService {
        val objectMapper = jacksonObjectMapper().apply {
            findAndRegisterModules()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, properties.failOnUnknownProperties)
        }

        val restTemplate = RestTemplateBuilder()
            .rootUri(properties.talk.rootUri)
            .additionalInterceptors(*interceptors.toTypedArray())
            .messageConverters(
                StringHttpMessageConverter(StandardCharsets.UTF_8),
                AllEncompassingFormHttpMessageConverter(),
                MappingJackson2HttpMessageConverter(objectMapper)
            )
            .also { builder ->
                for (customizer in customizers) {
                    customizer.customize(builder)
                }
            }
            .build()

        return AligoTalkService(restTemplate, objectMapper, properties, tokenStore)
    }
}
