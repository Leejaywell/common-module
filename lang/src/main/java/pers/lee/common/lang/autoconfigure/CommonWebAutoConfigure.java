package pers.lee.common.lang.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by Passyt on 2018/5/16.
 */
@Configuration
@Import({CrossDomainFilterConfiguration.class, RedirectWebConfiguration.class})
public class CommonWebAutoConfigure {
}
