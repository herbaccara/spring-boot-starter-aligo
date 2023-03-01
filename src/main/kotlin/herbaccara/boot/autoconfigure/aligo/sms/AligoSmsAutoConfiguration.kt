package herbaccara.boot.autoconfigure.aligo.sms

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.aligo.sms.AligoSmsService
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
@EnableConfigurationProperties(AligoSmsProperties::class)
@ConditionalOnProperty(prefix = "aligo.sms", value = ["enabled"], havingValue = "true")
class AligoSmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().apply {
            findAndRegisterModules()
        }
    }

    @Bean
    fun aligoSmsService(
        objectMapper: ObjectMapper,
        customizers: List<AligoSmsRestTemplateBuilderCustomizer>,
        interceptors: List<AligoSmsClientHttpRequestInterceptor>,
        properties: AligoSmsProperties
    ): AligoSmsService {
        val restTemplate = RestTemplateBuilder()
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

        return AligoSmsService(restTemplate, objectMapper, properties)
    }
}
