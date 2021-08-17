package io.metersphere.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 *
 * @author 3Dnn
 * @date 2021/02/14
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 供kubernetes探针问候
     * http 200表示成功
     *
     * @return
     */
    @GetMapping(value = "")
    public String HowAreYou() {
        return "I'm fine. Thank you :)";
    }
}
