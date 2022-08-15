package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@SpringBootApplication
public class CodeSharingPlatform {
//    CodeStack stack = new CodeStack();

    public static void main(String[] args) {
        SpringApplication.run(CodeSharingPlatform.class, args);
        System.out.println("this is running!!");
    }


    @RestController
    public class CodeController {
        @Autowired
        private CodeRepository codeRepository;
        @GetMapping("/api/code/{id}")
        public HashMap<String, Object> getCode(@PathVariable String id) {
            Optional<Code> code = codeRepository.findById(UUID.fromString(id));
//            String timeLeft = code.get().getCodeMap().get("time").toString();

            if (code.isPresent() && code.get().checkTime() && code.get().checkViews()) {
//            if ((!code.get().isTimeRestricted() || (Integer.parseInt(timeLeft) > 0)) && (code.isPresent())) {
                code.get().viewMinusOne();
                System.out.println("/api/code/" + id + " " + code);
                codeRepository.save(code.get());
                return code.get().getCodeMap();
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//            return (code.isPresent()) ? code.get().getCodeMap() : result;
//            result.put("code", code.getCodeMap());
        }
        @GetMapping("/code/{id}")
        public String getCodeHtml(@PathVariable String id) {
            Optional<Code> code = codeRepository.findById(UUID.fromString(id));
//            String timeLeft = code.get().getCodeMap().get("time").toString();
            if (code.isPresent() && code.get().checkTime() && code.get().checkViews()) {
                code.get().viewMinusOne();
                System.out.println("/code/" + id + " " + code);
                codeRepository.save(code.get());
                return code.get().getCodeHtml();
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);


//            return (code.isPresent()) ? code.get().getCodeHtml() : (id + " not found.");
//            return (stack.getCodeById(id) != null) ? stack.getCodeById(id).getCodeHtml() : (id + " not found.");
//            return id + " Not found";
        }

        @GetMapping("/api/code/latest")
        public List<HashMap<String, Object>> getLatest() {
            Iterable<Code> lastest = codeRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));

            List<HashMap<String, Object>> result = new ArrayList<>();
            lastest.forEach(last -> {
                if (result.size() < 10 && !(last.isTimeRestricted() || last.isViewRestricted())){
                    result.add(last.getCodeMap());
                }
            });
            System.out.println("/api/code/latest");
            return result;
        }
        @GetMapping("/code/latest")
        public String getLatestHtml() {
            Iterable<Code> lastest = codeRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
            List<HashMap<String, Object>> result = new ArrayList<>();
            lastest.forEach(last -> {
                if (!(last.isViewRestricted() || last.isTimeRestricted())) result.add(last.getCodeMap());
            });

            String highlighting = "<link rel=\"stylesheet\"\n" +
                "       target=\"_blank\" target=\"_blank\" target=\"_blank\" target=\"_blank\" target=\"_blank\" href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
                "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
                "<script>hljs.initHighlightingOnLoad();</script>";
            String title = "Latest";
            String code = "";
            for (int i = 0; i < ((result.size() >= 10) ? 10 : result.size()); i++) {
                code +=  "<span id=\"load_date\">"+ result.get(i).get("date") + "</span>";
                code += "<pre id=\"code_snippet\">"+ result.get(i).get("code") + "</pre>";
                code += "<br>";
            }
            String template = "<html><head><title>" +
                title +
                "</title>"+highlighting+"</head><body>" +
                code +
                "</body></html>";
            System.out.println("/code/latest");
            return template;
        }
        @GetMapping("/code/reset")
        public String reset(){
            codeRepository.deleteAll();
            return "Database cleared";
        }

        @PostMapping("/api/code/new")
        public Map<String, UUID> postJsonCode(@RequestBody Map<String, String> newCode) {
            Code code = new Code(
                newCode.get("code"),
                Integer.parseInt(newCode.get("time")),
                Integer.parseInt(newCode.get("views")));
            code = codeRepository.save(code);
            System.out.println("/api/code/new " + code.getIdMap());
            return code.getIdMap();

        }
        @GetMapping("/code/new")
        public String postCode() {
            return Code.htmlForm();
        }

    }
}
