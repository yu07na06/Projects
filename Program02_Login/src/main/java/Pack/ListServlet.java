package Pack;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/list")
public class ListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ListServlet() {
        super();
        System.out.println("생성자 콜");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet");
		response.setContentType("text/html; charset=UTF-8"); // 한글 설정
		String id = request.getParameter("id");
		System.out.println(id);
		PrintWriter out = response.getWriter();
		out.println("<html> <body>");
		String[] name = new String[]{"홍길동", "이순신", "유관순"};
		out.println("<h1> 회원목록 </h1>");
		out.println("<table border=\"2\" style=\"width: 20%\">");
		for (String n : name) {
			out.println("<tr>");
			out.println("<th>"+n+"</th>");
			out.println("<th><a href='delete?"+"id="+n+"'\">"+"삭제</a>"+"</th>");
			out.println("<th><a href='update?"+"id="+n+"'\">"+"수정</a>"+"</th>");
			out.println("</tr>");
		}
		out.println("</body> </html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}