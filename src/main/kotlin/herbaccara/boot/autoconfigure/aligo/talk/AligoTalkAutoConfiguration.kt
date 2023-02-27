package herbaccara.boot.autoconfigure.aligo.talk

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.aligo.talk.AligoTalkService
import herbaccara.aligo.talk.store.AligoTalkInMemoryTokenStore
import herbaccara.aligo.talk.store.AligoTalkTokenStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.util.*

@AutoConfiguration
@EnableConfigurationProperties(AligoTalkProperties::class)
@ConditionalOnProperty(prefix = "aligo.talk", value = ["enabled"], havingValue = "true")
class AligoTalkAutoConfiguration {

    @Bean("aligoTalkObjectMapper")
    fun objectMapper(properties: AligoTalkProperties): ObjectMapper {
        return jacksonObjectMapper().apply {
            findAndRegisterModules()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, properties.failOnUnknownProperties)
        }
    }

    @Bean("aligoTalkRestTemplate")
    fun restTemplate(
        properties: AligoTalkProperties,
        @Qualifier("aligoTalkObjectMapper") objectMapper: ObjectMapper,
        customizers: List<AligoTalkRestTemplateBuilderCustomizer>,
        interceptors: List<AligoTalkClientHttpRequestInterceptor>
    ): RestTemplate {
        return RestTemplateBuilder()
            .rootUri(properties.rootUri)
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
    }

    @Bean
    @ConditionalOnMissingBean(AligoTalkTokenStore::class)
    fun aligoTalkTokenStore(): AligoTalkTokenStore {
        return AligoTalkInMemoryTokenStore()
    }

    @Bean
    fun aligoTalkService(
        @Qualifier("aligoTalkRestTemplate") restTemplate: RestTemplate,
        @Qualifier("aligoTalkObjectMapper") objectMapper: ObjectMapper,
        properties: AligoTalkProperties,
        tokenStore: AligoTalkTokenStore
    ): AligoTalkService {
        return AligoTalkService(restTemplate, objectMapper, properties, tokenStore)
    }
}
