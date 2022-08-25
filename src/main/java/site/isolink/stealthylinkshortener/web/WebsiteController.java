package site.isolink.stealthylinkshortener.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Common purpose controller.
 */
@Slf4j
@Controller
public class WebsiteController {
    /**
     * Returns favicon location.
     * @return favicon location
     */
    @GetMapping("/favicon.ico")
    String getFavicon() {
        return "forward:/icons/favicon.ico";
    }
}
