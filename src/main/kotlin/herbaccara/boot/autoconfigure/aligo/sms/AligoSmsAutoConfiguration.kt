package herbaccara.boot.autoconfigure.aligo.sms

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.aligo.sms.AligoSmsService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
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
@EnableConfigurationProperties(AligoSmsProperties::class)
@ConditionalOnProperty(prefix = "aligo.sms", value = ["enabled"], havingValue = "true")
class AligoSmsAutoConfiguration {

    @Bean("aligoSmsObjectMapper")
    fun objectMapper(properties: AligoSmsProperties): ObjectMapper {
        return jacksonObjectMapper().apply {
            findAndRegisterModules()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, properties.failOnUnknownProperties)
        }
    }

    @Bean("aligoSmsRestTemplate")
    fun restTemplate(
        properties: AligoSmsProperties,
        @Qualifier("aligoSmsObjectMapper") objectMapper: ObjectMapper,
        customizers: List<AligoSmsRestTemplateBuilderCustomizer>,
        interceptors: List<AligoSmsClientHttpRequestInterceptor>
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
    fun aligoSmsService(
        @Qualifier("aligoSmsRestTemplate") restTemplate: RestTemplate,
        @Qualifier("aligoSmsObjectMapper") objectMapper: ObjectMapper,
        properties: AligoSmsProperties
    ): AligoSmsService {
        return AligoSmsService(restTemplate, objectMapper, properties)
    }
}
