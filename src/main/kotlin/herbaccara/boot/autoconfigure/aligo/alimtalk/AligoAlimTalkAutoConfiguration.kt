package herbaccara.boot.autoconfigure.aligo.alimtalk

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.aligo.alimtalk.AligoAlimTalkService
import herbaccara.aligo.alimtalk.store.AligoAlimTalkInMemoryTokenStore
import herbaccara.aligo.alimtalk.store.AligoAlimTalkTokenStore
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
@EnableConfigurationProperties(AligoAlimTalkProperties::class)
@ConditionalOnProperty(prefix = "aligo.alimtalk", value = ["enabled"], havingValue = "true")
class AligoAlimTalkAutoConfiguration {

    @Bean("aligoAlimTalkObjectMapper")
    fun objectMapper(properties: AligoAlimTalkProperties): ObjectMapper {
        return jacksonObjectMapper().apply {
            findAndRegisterModules()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, properties.failOnUnknownProperties)
        }
    }

    @Bean("aligoAlimTalkRestTemplate")
    fun restTemplate(
        properties: AligoAlimTalkProperties,
        @Qualifier("aligoAlimTalkObjectMapper") objectMapper: ObjectMapper,
        customizers: List<AligoAlimTalkRestTemplateBuilderCustomizer>,
        interceptors: List<AligoAlimTalkClientHttpRequestInterceptor>
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
    @ConditionalOnMissingBean(AligoAlimTalkTokenStore::class)
    fun aligoAlimTalkTokenStore(): AligoAlimTalkTokenStore {
        return AligoAlimTalkInMemoryTokenStore()
    }

    @Bean
    fun aligoSmsService(
        @Qualifier("aligoAlimTalkRestTemplate") restTemplate: RestTemplate,
        @Qualifier("aligoAlimTalkObjectMapper") objectMapper: ObjectMapper,
        properties: AligoAlimTalkProperties,
        tokenStore: AligoAlimTalkTokenStore
    ): AligoAlimTalkService {
        return AligoAlimTalkService(restTemplate, objectMapper, properties, tokenStore)
    }
}
