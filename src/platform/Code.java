package platform;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity(name = "code")
//@Table
public class Code {
	public int getTime() {
		return time;
	}

	private int time;
	private int views;
	@Column
	private String code;
	@Id
	@Type(type = "uuid-char")
	@ColumnDefault("random_uuid()")
	@GeneratedValue
	private UUID id;
	@Column
	LocalDateTime date;

	@Column
	private boolean isTimeRestricted;

	public boolean isViewRestricted() {
		return isViewRestricted;
	}

	@Column
	private boolean isViewRestricted;


	public boolean isTimeRestricted() {
		return isTimeRestricted;
	}

	public Code(String code, int time, int views) {
		this.date = LocalDateTime.now();
		this.code = code;
		// restriction should be individualized
		this.isTimeRestricted = time > 0;
		this.isViewRestricted = views > 0;
		this.time = time;
		this.views = views;

	}

	public Code() {
	}

	public HashMap<String, Object> getCodeMap() {
		// this is the response for the API
		HashMap<String, Object> map = new HashMap<>();
		map.put("date", this.prettyDate());
		map.put("code", code);
		map.put("time", isTimeRestricted ?
				Duration.between(LocalDateTime.now(), this.date.plusSeconds(this.time)).toSeconds() : time);
		map.put("views", this.views);
		return map;
	}

	public String getCodeHtml() {
		// this is the response in HTML format
		String highlighting = "<link rel=\"stylesheet\"\n" +
				"       target=\"_blank\" target=\"_blank\" target=\"_blank\" target=\"_blank\" target=\"_blank\" href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
				"<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
				"<script>hljs.initHighlightingOnLoad();</script>";
		String title = "Code";
		String dateSpam = "<span id=\"load_date\">" + this.prettyDate() + "</span>";
		String codePre = "<pre id=\"code_snippet\"><code>"+ this.code +	"</code></pre>";
		String timeLeft = Integer.toString(isTimeRestricted ?
				(int) Duration.between(LocalDateTime.now(), this.date.plusSeconds(this.time)).toSeconds() : time);
		String timeRestriction = isTimeRestricted ? "<span id=\"time_restriction\"> Time left: " + timeLeft + "</span>" : "";
		String viewRestriction = isViewRestricted ? "<span id=\"views_restriction\"> Views left: " + views +"</span>" : "";
		String template = "<html><head><title>" +
				title +
				"</title>"+highlighting+"</head><body>" +
				dateSpam + timeRestriction + viewRestriction +
				codePre +
				"</body></html>";
		return template;
	}

	String prettyDate() {
		int year = this.date.getYear();
		int month = this.date.getMonthValue();
		int day = this.date.getDayOfMonth();
		int hour = this.date.getHour();
		int minutes = this.date.getMinute();
		int seconds = this.date.getSecond();

		return year + "/" + this.prettyNumber(month) + "/" + this.prettyNumber(day) + " " + this.prettyNumber(hour) + ":" + this.prettyNumber(minutes) +
				":" + this.prettyNumber(seconds);
	}
	String prettyNumber(int num) {
		return (num < 10) ? "0" + num : "" + num;
	}

	static String htmlForm() {
		String title = "Create";
		String textarea = "<textarea id=\"code_snippet\" placeholder=\"//write your code here\"></textarea>";
		String button = "<br><button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>";
		String timeRestriction = "<input id=\"time_restriction\" type=\"text\" placeholder=\"Time\"/>";
		String viewRestriction = "<input id=\"views_restriction\" type=\"text\" placeholder=\"Views\"/>";
		String functionSend = "<script>" +
				"function send() {\n" +
				"    let object = {\n" +
				"        \"code\": document.getElementById(\"code_snippet\").value,\n" +
				"        \"views\": document.getElementById(\"views_restriction\").value,\n" +
				"        \"time\": document.getElementById(\"time_restriction\").value\n" +
				"    };\n" +
				"    \n" +
				"    let json = JSON.stringify(object);\n" +
				"    \n" +
				"    let xhr = new XMLHttpRequest();\n" +
				"    xhr.open(\"POST\", '/api/code/new', false)\n" +
				"    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');\n" +
				"    xhr.send(json);\n" +
				"    \n" +
				"    if (xhr.status == 200) {\n" +
				"      alert(\"Success!\");\n" +
				"    }\n" +
				"}\n" +
				"</script>\n";
		String form = "<form method=\"post\" action=\"/api/code/new\">" +
				textarea + timeRestriction + viewRestriction + button +
				"</form>";
		String template = "<html><head><title>" +
				title +
				"</title></head><body>" +
				form +
				"</body>" +
				functionSend + "</html>";
		return template;
	}
	public Map<String, UUID> getIdMap() {
		Map<String, UUID> map = new HashMap<>();
		map.put("id", this.id);
		return map;
	}

	public LocalDateTime getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "Code{" +
				"code='" + code + '\'' +
				", id=" + id +
				", date=" + date +
				'}';
	}
	public void viewMinusOne() {
		if (isViewRestricted) {
			this.views -= 1;
		}
	}
	public boolean checkTime() {
		return !isTimeRestricted || Duration.between(LocalDateTime.now(), date.plusSeconds(time)).toSeconds() > 0;
	}
	public boolean checkViews() {
		return !isViewRestricted || views > 0;
	}

}
