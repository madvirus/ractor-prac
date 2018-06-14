package simple;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
public class HelloController {
    @GetMapping("/hello")
    public String hello(Model model, @RequestParam("name") String name) {
        model.addAttribute("greeting", "인사");
        model.addAttribute("name", name);
        return "hello";
    }

    @GetMapping("/hello2")
    public Mono<String> hello2(Model model, @RequestParam("name") String name) {
        model.addAttribute("greeting", "인사");
        model.addAttribute("name", name);
        return Mono.just("hello");
    }
}
