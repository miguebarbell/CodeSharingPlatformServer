package platform;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeStack {

	private List<Code> codeStack;
	public CodeStack() {
		// todo: get code from database
		this.codeStack = new ArrayList<>();
	}
	public List<Code> getCodeStack() {
		return this.codeStack;
	}
	public int generateId() {
		return codeStack.size() + 1;
	}


	public List<HashMap<String, Object>> getLastTen() {
		int size = Math.min(this.codeStack.size(), 10);
		List<HashMap<String, Object>> lastTen = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			lastTen.add(this.codeStack.get(i).getCodeMap());
		}
		return lastTen;
	}
	public String getLastTenHtml() {
		String highlighting = "<link rel=\"stylesheet\"\n" +
				"       target=\"_blank\" target=\"_blank\" target=\"_blank\" target=\"_blank\" target=\"_blank\" href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
				"<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
				"<script>hljs.initHighlightingOnLoad();</script>";
		String title = "Latest";
		String code = "";
		List<HashMap<String, Object>> lastTen = this.getLastTen();

		for (int i = 0; i < lastTen.size(); i++) {
			code +=  "<span id=\"load_date\">"+ lastTen.get(i).get("date") + "</span>";
			code += "<pre id=\"code_snippet\">"+ lastTen.get(i).get("code") + "</pre>";
			code += "<br>";
		}
		String template = "<html><head><title>" +
				title +
				"</title>"+highlighting+"</head><body>" +
				code +
				"</body></html>";
		return template;
	}
}
