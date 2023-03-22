package herbaccara.boot.autoconfigure.aligo.sms

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.aligo.sms.AligoSmsService
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
@ConditionalOnProperty(prefix = "aligo.sms", value = ["enabled"], havingValue = "true")
class AligoSmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun aligoSmsService(
        customizers: List<AligoSmsRestTemplateBuilderCustomizer>,
        interceptors: List<AligoSmsClientHttpRequestInterceptor>,
        properties: AligoProperties
    ): AligoSmsService {
        val objectMapper = jacksonObjectMapper().apply {
            findAndRegisterModules()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, properties.failOnUnknownProperties)
        }

        val restTemplate = RestTemplateBuilder()
            .rootUri(properties.sms.rootUri)
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

        return AligoSmsService(restTemplate, objectMapper, properties)
    }
}
